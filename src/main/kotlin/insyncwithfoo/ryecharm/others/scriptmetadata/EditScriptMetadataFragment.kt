package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
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
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.writeUnderAction
import kotlinx.coroutines.CoroutineScope
import org.toml.lang.psi.TomlFile


private typealias InjectedAndHost = Pair<TomlFile, PyFile>


private operator fun <A, B> com.intellij.openapi.util.Pair<A, B>.component1() = first
private operator fun <A, B> com.intellij.openapi.util.Pair<A, B>.component2() = second


private val Project.injectedLanguageManager: InjectedLanguageManager
    get() = InjectedLanguageManager.getInstance(this)


private val PsiElement.hostFile: PsiFile
    get() = project.injectedLanguageManager.getTopLevelFile(this)


private val PsiElement.host: PsiElement?
    get() = project.injectedLanguageManager.getInjectionHost(this)


private val PsiElement.injectedFiles: List<PsiElement>
    get() = project.injectedLanguageManager.getInjectedPsiFiles(this)?.map { (element, _) -> element }
        ?: emptyList()


private val PsiElement.isAtLineStart: Boolean
    get() {
        val file = containingFile ?: return false
        val document = file.viewProvider.document ?: return false
        val line = document.getLineNumber(startOffset)
        
        return document.getLineStartOffset(line) == startOffset
    }


private val PyFile.injectedPEP723Fragment: TomlFile?
    get() {
        val blockStart = findScriptBlock().firstOrNull()
        
        return blockStart?.injectedFiles?.singleOrNull() as? TomlFile
    }


private fun Editor.addReleaseListener(project: Project, listener: (Document) -> Unit) {
    val parentDisposable = RootDisposable.getInstance(project)
    
    editorFactory.addEditorFactoryListener(object : EditorFactoryListener {
        override fun editorReleased(event: EditorFactoryEvent) {
            if (event.editor === this@addReleaseListener) {
                listener(event.editor.document)
            }
        }
    }, parentDisposable)
}


// TODO: Make this DumbAware?
/**
 * Allow editing a PEP 723 block injected fragment
 * in a new editor similar to a normal TOML file.
 * 
 * @see com.intellij.codeInsight.intention.impl.QuickEditAction
 */
internal class EditScriptMetadataFragment : IntentionAction, LowPriorityAction {
    
    override fun startInWriteAction() = false
    
    override fun getFamilyName() = message("intentions.main.editScriptMetadataFragment.familyName")
    
    override fun getText() = familyName
    
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
        val offsetInFragment = when (file) {
            is TomlFile -> editor.caretModel.offset
            is PyFile -> 0
            else -> return
        }
        
        val fragmentEditor = project.openNewEditor(injectedFile, hostFileName, offsetInFragment) ?: return
        
        fragmentEditor.addReleaseListener(project) { document ->
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
     * @see ScriptMetadataSchemaProvider.isAvailable
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
