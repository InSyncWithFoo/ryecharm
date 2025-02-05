package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.impl.PyPsiUtils
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.ZeroBasedIndex


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
internal class RuffDisableRuleComment(private val code: RuleCode, private val offset: ZeroBasedIndex) :
    LocalQuickFix, HighPriorityAction {
    
    override fun getFamilyName() = message("intentions.ruff.disableRuleComment.familyName")
    
    override fun getName() = message("intentions.ruff.disableRuleComment.name", code)
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        
        val noqaComment = file.findSameLineComment(offset)
            ?.let { NoqaComment.parse(it) }
        
        when (noqaComment) {
            null -> file.edit { it.appendNewCommentToLine() }
            else -> file.edit { it.addNewCodeToExistingComment(noqaComment) }
        }
    }
    
    private fun Document.appendNewCommentToLine() {
        val lineNumber = getLineNumber(offset)
        val lastCharacter = charsSequence.getOrNull(getLineEndOffset(lineNumber) - 1)
        
        val padding = when {
            lastCharacter == ' ' -> ""
            else -> "  "
        }
        
        appendToLine(lineNumber, padding + NoqaComment.fromCode(code))
    }
    
    private fun Document.addNewCodeToExistingComment(comment: NoqaComment) {
        replaceString(comment.start, comment.end, comment.withNewCode(code))
    }
    
}
