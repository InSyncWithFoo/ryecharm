package insyncwithfoo.ryecharm

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile


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
