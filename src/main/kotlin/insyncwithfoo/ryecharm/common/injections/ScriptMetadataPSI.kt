package insyncwithfoo.ryecharm.common.injections

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.python.psi.PyFile


internal val scriptBlock = """(?mx)
    ^\#\x20///\x20script\n
    (?<content>
        \#(?:\x20.*)?
        (?:\n\#(?:\x20.*)?)*
    )
    \n
    \#\x20///${'$'}
""".toRegex()


internal val PsiElement.isStartBlockLine: Boolean
    get() = this is PsiComment && textMatches("# /// script")


internal val PsiElement.isEmptyBlockLine: Boolean
    get() = this is PsiComment && textMatches("#")


internal val PsiElement.isBlockLine: Boolean
    get() = this is PsiComment && (this.isEmptyBlockLine || text.startsWith("# "))


internal val PsiElement.isLineBreak: Boolean
    get() = this is PsiWhiteSpace && textMatches("\n")


private val PyFile.topLevelComments: Sequence<PsiComment>
    get() = children.asSequence().filterIsInstance<PsiComment>()


/**
 * Search for the first block matching [scriptBlock],
 * then return a [Sequence] of all [PsiComment]s constituting its body
 * (that is, without the starting `# /// script` and the ending `# ///`).
 *
 * If such a block is not found, return an empty sequence.
 */
internal fun PyFile.findScriptBlock(): Sequence<PsiComment> {
    val document = viewProvider.document ?: return emptySequence()
    val match = scriptBlock.find(document.charsSequence) ?: return emptySequence()
    
    val contentGroupRange = match.groups["content"]!!.range
    val (contentStart, contentEnd) = Pair(contentGroupRange.first, contentGroupRange.last)
    
    val firstComment = findElementAt(contentStart) as? PsiComment ?: return emptySequence()
    val lastComment = findElementAt(contentEnd) as? PsiComment ?: return emptySequence()
    
    val firstUntilLast = topLevelComments
        .dropWhile { it != firstComment }
        .takeWhile { it != lastComment }
    
    return firstUntilLast + sequenceOf(lastComment)
}
