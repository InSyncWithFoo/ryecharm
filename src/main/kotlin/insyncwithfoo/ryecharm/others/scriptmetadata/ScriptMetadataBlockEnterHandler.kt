package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate.Result
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile


private val possiblyEmptyScriptBlock = """(?mx)
    ^\#\x20///\x20script
    (?<content>
        \n\#(?:\x20.*)?
        (?:\n\#(?:\x20.*)?)*
    )?
    \n
    \#\x20///$
""".toRegex()


/**
 * Insert `# ` when the user presses Enter
 * in the middle of a script metadata block.
 * 
 * Before:
 * 
 * ```python
 * # /// script
 * # foo = true|
 * # ///
 * ```
 * 
 * After:
 * 
 * ```python
 * # /// script
 * # foo = true
 * # |
 * # ///
 * ```
 */
internal class ScriptMetadataBlockEnterHandler : EnterHandlerDelegate {
    
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffset: Ref<Int>,
        caretAdvance: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): Result {
        if (file !is PyFile) {
            return Result.Continue
        }
        
        val offset = caretOffset.get() ?: return Result.Continue
        val document = file.viewProvider.document
        val text = document.charsSequence
        
        val block = possiblyEmptyScriptBlock.find(text) ?: return Result.Continue
        
        if (offset in block.range) {
            document.insertString(offset, "# ")
            caretAdvance.set(caretAdvance.get() + "# ".length)
            return Result.Default
        }
        
        return Result.Continue
    }
    
    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): Result {
        return Result.Continue
    }
    
}
