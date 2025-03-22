package insyncwithfoo.ryecharm

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil


/**
 * A constant to be used as the return value of
 * [PsiTreeUtil.processElements]'s callback.
 */
private const val CONTINUE_PROCESSING = true


/**
 * Traverse the PSI tree whose root is [this]
 * and invoke [callback] on each element of type [T].
 */
internal inline fun <reified T : PsiElement> PsiElement.traverse(crossinline callback: (T) -> Unit) {
    PsiTreeUtil.processElements(this, T::class.java) { element ->
        callback(element)
        CONTINUE_PROCESSING
    }
}


internal fun PsiFile.findCommentAt(offset: Int) =
    findElementAt(offset) as? PsiComment


/**
 * Return the comment at [offset] or the offset right before that, if any.
 * 
 * This is helpful because the position at the very end
 * of a Python comment is not considered a part of it
 * but of the "newline" whitespace element.
 * 
 * ```python
 * # Foo|
 * ```
 */
internal fun PsiFile.findCommentAtOrNearby(offset: Int) =
    findCommentAt(offset) ?: findCommentAt(offset - 1)
