package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.RootDisposable
import insyncwithfoo.ryecharm.editorFactory
import insyncwithfoo.ryecharm.fileEditorManager
import insyncwithfoo.ryecharm.host
import insyncwithfoo.ryecharm.hostFile
import insyncwithfoo.ryecharm.injectedFiles
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.writeUnderAction
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.VisibleForTesting
import org.toml.lang.psi.TomlFile
import kotlin.math.max


private typealias InjectedAndHost = Pair<TomlFile, PyFile>


private val PsiElement.isAtLineStart: Boolean
    get() {
        val file = containingFile ?: return false
        val document = file.viewProvider.document ?: return false
        val line = document.getLineNumber(startOffset)
        
        return document.getLineStartOffset(line) == startOffset
    }


private val PyFile.injectedPEP723Fragment: TomlFile?
    get() {
        val blockStart = findScriptBlockBodyElements().firstOrNull()
        
        return blockStart?.injectedFiles?.singleOrNull() as? TomlFile
    }


private fun Editor.addReleaseListener(listener: (Document) -> Unit) {
    val parentDisposable = Disposer.newDisposable()
    
    Disposer.register(RootDisposable.getInstance(), parentDisposable)
    
    editorFactory.addEditorFactoryListener(object : EditorFactoryListener {
        override fun editorReleased(event: EditorFactoryEvent) {
            if (event.editor === this@addReleaseListener) {
                listener(event.editor.document)
                Disposer.dispose(parentDisposable)
            }
        }
    }, parentDisposable)
}


/**
 * Calculate the offset the new editor should put its cursor at.
 * 
 * * If assumptions are incorrect (which should never happen), return -1.
 * * If the cursor is placed within the start line, return 0.
 * * If the cursor is placed within the end line,
 *   return the length of the temporary file (i.e., the max offset),
 *   which would put the new cursor on the automatically added empty line
 *   at the end of the file.
 * * Otherwise:
 *     * The new row is calculated by subtracting
 *       the start line's index plus 1 from the body line index.
 *     * The new column is calculated by saturatedly substracting
 *       2 from the original column.
 *     * Return the offset that would correspond to
 *       the pinpoint located by these two indices.
 * 
 * ```python
 * # /// script        # Start line
 * # foo = ["bar"]     # Body line
 * # ///               # End line
 * ```
 */
@VisibleForTesting
internal fun PyFile.calculateCursorOffsetInFragment(editor: Editor): Int {
    val offset = editor.caretModel.offset
    val document = viewProvider.document ?: return -1
    val block = scriptBlock.find(document.charsSequence) ?: return -1
    val blockRange = block.range
    
    if (offset < blockRange.first || offset > blockRange.last + 1) {
        return -1
    }
    
    val blockLines = block.value.split("\n")
    val blockBodyLines = blockLines.subList(1, blockLines.size - 1)
    
    val line = document.getLineNumber(offset)
    val blockStartLine = document.getLineNumber(blockRange.first)
    val lineInFragment = line - blockStartLine - 1
    
    if (lineInFragment < 0) {
        return 0
    }
    
    val lineStart = document.getLineStartOffset(line)
    val column = offset - lineStart
    val columnInFragment = max(0, column - "# ".length)
    
    var offsetInFragment = when (lineInFragment < blockBodyLines.size) {
        true -> columnInFragment
        else -> 0
    }
    
    for (blockLine in blockBodyLines.slice(0..<lineInFragment)) {
        val blockLineLengthInFragment = max(0, blockLine.length - "# ".length)
        
        offsetInFragment += blockLineLengthInFragment + "\n".length
    }
    
    return offsetInFragment
}


/**
 * Allow editing a PEP 723 block injected fragment
 * in a new editor similar to a normal TOML file.
 * 
 * @see com.intellij.codeInsight.intention.impl.QuickEditAction
 */
internal class EditScriptMetadataFragment : IntentionAction, LowPriorityAction, DumbAware {
    
    override fun startInWriteAction() = false
    
    override fun getFamilyName() = message("intentions.main.editScriptMetadataFragment.familyName")
    
    override fun getText() = familyName
    
    override fun generatePreview(project: Project, editor: Editor, file: PsiFile) =
        IntentionPreviewInfo.EMPTY!!
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = when {
        editor == null || file == null -> false
        file is PyFile -> file.cursorIsInsidePEP723Fragment(editor)
        file is TomlFile -> file.hostFile is PyFile && file.isPEP723Fragment
        else -> false
    }
    
    private fun PyFile.cursorIsInsidePEP723Fragment(editor: Editor): Boolean {
        val offset = editor.caretModel.offset
        val document = viewProvider.document ?: return false
        val blockRange = scriptBlock.find(document.charsSequence)?.range ?: return false
        
        return blockRange.first <= offset && offset <= blockRange.last + 1
    }
    
    private val TomlFile.isPEP723Fragment: Boolean
        get() = host?.isPEP723FragmentHost ?: false
    
    private val PsiElement.isPEP723FragmentHost: Boolean
        get() {
            val thisComment = (this as? PsiComment)?.takeIf { it.isBlockLine }
            val precedingLineBreak = thisComment?.prevSibling?.takeIf { it.isLineBreak }
            val blockStart = precedingLineBreak?.prevSibling?.takeIf { it.isStartBlockLine }
            
            return blockStart?.isAtLineStart == true
        }
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null) {
            return
        }
        
        val (injectedFile, hostFile) = file.injectedAndHost ?: return
        
        val hostFileName = hostFile.viewProvider.virtualFile.name
        val offsetInFragment = hostFile.calculateCursorOffsetInFragment(editor)
        
        val fragmentEditor = project.openNewEditor(injectedFile, hostFileName, offsetInFragment) ?: return
        
        fragmentEditor.addReleaseListener { document ->
            val newBlock = document.charsSequence.removeSuffix("\n").asPEP723Block()
            val oldContent = hostFile.viewProvider.document?.charsSequence ?: return@addReleaseListener
            val newContent = oldContent.replace(scriptBlock, newBlock)
            
            val title = message("intentions.main.editScriptMetadataFragment.progressTitle")
            
            project.writeUnderAction<Coroutine>(title, hostFile, newContent)
        }
    }
    
    private val PsiFile.injectedAndHost: InjectedAndHost?
        get() = when (this) {
            is TomlFile -> this.withHost
            is PyFile -> this.withInjected
            else -> null
        }
    
    private val TomlFile.withHost: InjectedAndHost?
        get() = (hostFile as? PyFile)?.let { Pair(this, it) }
    
    private val PyFile.withInjected: InjectedAndHost?
        get() = injectedPEP723Fragment?.let { Pair(it, this) }
    
    private fun Project.openNewEditor(injectedFile: TomlFile, hostFilename: String, offset: Int): Editor? {
        val newVirtualFile = injectedFile.asNewVirtualFile(hostFilename) ?: return null
        
        val descriptor = OpenFileDescriptor(this, newVirtualFile, offset)
        val focusEditor = true
        
        return fileEditorManager.openTextEditor(descriptor, focusEditor)
    }
    
    /**
     * @see insyncwithfoo.ryecharm.isScriptMetadataTemporaryFile
     */
    private fun TomlFile.asNewVirtualFile(hostFilename: String): VirtualFile? {
        val name = message("intentions.main.editScriptMetadataFragment.filename", hostFilename)
        val content = viewProvider.document?.charsSequence ?: return null
        
        return LightVirtualFile(name, content)
    }
    
    private fun CharSequence.asPEP723Block(): String {
        val lines = this.split("\n")
        val prefixedLines = lines.joinToString("\n") { it.asPEP723BlockLine() }
        
        return """
            |# /// script
            |$prefixedLines
            |# ///
        """.trimMargin()
    }
    
    private fun String.asPEP723BlockLine() = when (this.isEmpty()) {
        true -> "#"
        else -> "# $this"
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
