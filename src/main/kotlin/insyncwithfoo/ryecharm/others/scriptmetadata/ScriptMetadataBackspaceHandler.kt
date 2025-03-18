package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile


private const val STOP_FURTHER_PROCESSING = true
private const val CONTINUE_PROCESSING = false


private typealias LineStartOffset = Int
private typealias CursorOffset = Int


/**
 * Remove the leading `#` when the user presses Backspace
 * in an empty line of a script metadata block.
 * 
 * Before:
 * 
 * ```python
 * # /// script
 * # foo = "bar"
 * # |
 * # ///
 * ```
 * 
 * After:
 * 
 * ```python
 * # /// script
 * # foo = "bar"|
 * # ///
 * ```
 */
internal class ScriptMetadataBackspaceHandler : BackspaceHandlerDelegate() {
    
    override fun beforeCharDeleted(char: Char, file: PsiFile, editor: Editor) {
        handleDeletion(char, file, editor, expectedChar = '#') { document, lineStart, offset ->
            document.deleteString(lineStart, offset)
        }
    }
    
    override fun charDeleted(char: Char, file: PsiFile, editor: Editor) =
        handleDeletion(char, file, editor, expectedChar = ' ') { document, lineStart, offset ->
            val previousLineEnd = lineStart - 1
            document.deleteString(previousLineEnd, offset)
        }
    
    private inline fun handleDeletion(
        char: Char,
        file: PsiFile,
        editor: Editor,
        expectedChar: Char,
        modifyDocument: (Document, LineStartOffset, CursorOffset) -> Unit
    ): Boolean {
        if (file !is PyFile) {
            return CONTINUE_PROCESSING
        }
        
        if (char != expectedChar) {
            return CONTINUE_PROCESSING
        }
        
        val offset = editor.caretModel.offset
        
        val document = file.viewProvider.document ?: return CONTINUE_PROCESSING
        val blockRange = scriptBlock.find(document.charsSequence)?.range ?: return CONTINUE_PROCESSING
        
        if (offset !in blockRange) {
            return CONTINUE_PROCESSING
        }
        
        val line = document.getLineNumber(offset)
        val lineStart = document.getLineStartOffset(line)
        val column = offset - lineStart
        
        if (column != 1) {
            return CONTINUE_PROCESSING
        }
        
        modifyDocument(document, lineStart, offset)
        
        return STOP_FURTHER_PROCESSING
    }
    
}
