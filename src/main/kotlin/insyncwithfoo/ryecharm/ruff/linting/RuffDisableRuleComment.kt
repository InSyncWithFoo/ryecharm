package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import com.jetbrains.python.psi.impl.PyPsiUtils
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.ZeroBasedIndex
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.noqaComment
import insyncwithfoo.ryecharm.ruff.ruleCode
import org.jetbrains.annotations.VisibleForTesting


private class NoqaCommentFragment(val content: String, offset: Int) {
    val start = offset
    val end = start + content.length
}


private fun MatchGroup.toFragment(baseOffset: Int): NoqaCommentFragment {
    val selfRelativeOffset = range.first
    return NoqaCommentFragment(value, baseOffset + selfRelativeOffset)
}


// FIXME: Merge this.
/**
 * A fork of the synonymous class in
 * the `documentation.noqa` package.
 * 
 * They are only separated because their purposes
 * are not the same, and that the parsing algorithms for
 * file-level and line-level comments are subtly different.
 * 
 * @see insyncwithfoo.ryecharm.ruff.documentation.noqa.NoqaComment
 */
private class NoqaComment(private val codes: Set<RuleCode>) {
    
    private val codeList: String
        get() = codes.joinToString(", ")
    
    override fun toString() = when {
        codes.isEmpty() -> "# noqa"
        else -> "# noqa: $codeList"
    }
    
    companion object {
        fun parse(codeList: String): NoqaComment {
            val codes = ruleCode.findAll(codeList)
                .mapTo(mutableSetOf()) { match -> match.value }
            
            return NoqaComment(codes)
        }
    }
    
}


@VisibleForTesting
internal class InEditorNoqaComment private constructor(
    private val prefix: NoqaCommentFragment,
    private val codeList: NoqaCommentFragment?,
    private val lastSeparator: String?
) {
    
    private val separator: String
        get() = lastSeparator ?: ", "
    
    val codeListReplacementOffsets: Pair<Int, Int>
        get() = when {
            codeList != null -> codeList.start to codeList.end
            else -> prefix.end to prefix.end
        }
    
    fun codeListWithNew(newCode: RuleCode) =
        codeList!!.content + separator + newCode
    
    companion object {
        fun create(text: String, baseOffset: Int): InEditorNoqaComment? {
            val noqaPart = noqaComment.find(text) ?: return null
            val groups = noqaPart.groups
            
            val prefix = groups["prefix"]!!.toFragment(baseOffset)
            val codeList = groups["codeList"]?.toFragment(baseOffset)
            val lastSeparator = groups["lastSeparator"]?.value
            
            return InEditorNoqaComment(prefix, codeList, lastSeparator)
        }
    }
    
}


private fun PsiFile.findSameLineComment(offset: Int): PsiComment? {
    return findElementAt(offset)?.let { PyPsiUtils.findSameLineComment(it) }
}


private fun Document.appendToLine(line: Int, value: String) {
    val lineEndOffset = getLineEndOffset(line)
    replaceString(lineEndOffset, lineEndOffset, value)
}


/**
 * Quick fix to either insert [code] into
 * the `noqa` comment near [offset] or
 * create such a comment at the end of
 * the line containing [offset].
 *
 * When there are existing codes, the last separator
 * will be used to separate the new element.
 * Otherwise, comma-space (`, `) is used.
 */
internal class RuffDisableRuleComment(private val code: String, private val offset: ZeroBasedIndex) : LocalQuickFix {
    
    override fun getFamilyName() = message("intentions.ruff.disableRuleComment.familyName")
    
    override fun getName() = message("intentions.ruff.disableRuleComment.name", code)
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        
        val inEditorNoqaComment = file.findSameLineComment(offset)
            ?.let { InEditorNoqaComment.create(it.text, it.startOffset) }
        
        when (inEditorNoqaComment) {
            null -> file.edit { it.appendNewCommentToLine() }
            else -> file.edit { it.addNewCodeToExistingComment(inEditorNoqaComment) }
        }
    }
    
    private fun Document.appendNewCommentToLine() {
        val lineNumber = getLineNumber(offset)
        val lastCharacter = charsSequence.getOrNull(getLineEndOffset(lineNumber) - 1)
        
        val padding = when {
            lastCharacter == ' ' -> ""
            else -> "  "
        }
        
        appendToLine(lineNumber, padding + NoqaComment.parse(code))
    }
    
    private fun Document.addNewCodeToExistingComment(comment: InEditorNoqaComment) {
        val (start, end) = comment.codeListReplacementOffsets
        
        replaceString(start, end, comment.codeListWithNew(code))
    }
    
}
