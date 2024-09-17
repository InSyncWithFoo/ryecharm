package insyncwithfoo.ryecharm

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyUtil


internal fun PsiFile.edit(callback: (Document) -> Unit) {
    PyUtil.updateDocumentUnblockedAndCommitted(this, callback)
}


internal fun PsiFile.paste(newText: CharSequence) {
    this.edit { it.replaceContentWith(newText) }
}


internal fun Document.replaceString(range: TextRange, value: CharSequence) {
    replaceString(range.startOffset, range.endOffset, value)
}


internal fun Document.replaceContentWith(newContent: CharSequence) {
    replaceString(0, textLength, newContent)
}
