package insyncwithfoo.ryecharm.ruff.intentions

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.replaceString
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.RuleCode


internal class ReenableRule : IntentionAction, HighPriorityAction, DumbAware {
    
    private lateinit var code: RuleCode
    
    override fun startInWriteAction() = true
    
    override fun getFamilyName() = message("intentions.ruff.reenableRule.familyName")
    
    override fun getText() = message("intentions.ruff.reenableRule.name", code)
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val offset = editor?.caretModel?.offset ?: return false
        val comment = file?.findCommentAtOrNearby(offset) ?: return false
        
        val noqaComment = NoqaComment.parse(comment) ?: return false
        
        code = noqaComment.findCodeAtOffset(offset) ?: return false
        
        return true
    }
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val offset = editor?.caretModel?.offset ?: return
        val element = file?.findCommentAtOrNearby(offset) ?: return
        
        val noqaComment = NoqaComment.parse(element) ?: return
        val code = noqaComment.findCodeAtOffset(offset) ?: return
        val newContent = noqaComment.withoutCode(code)
        
        if (newContent.isNotEmpty() || noqaComment.start != element.startOffset) {
            file.edit { it.replaceString(noqaComment.range, newContent) }
            return
        }
        
        val restRange = noqaComment.toString().length..<element.textLength
        val restTrimmed = element.text.slice(restRange).trimStart()
        
        when (restTrimmed.startsWith("#") || restTrimmed.isEmpty()) {
            true -> file.edit { it.replaceString(element.textRange, restTrimmed) }
            else -> file.edit { it.replaceString(noqaComment.range, "#") }
        }
    }
    
    private fun PsiFile.findCommentAt(offset: Int) =
        findElementAt(offset) as? PsiComment
    
    private fun PsiFile.findCommentAtOrNearby(offset: Int) =
        findCommentAt(offset) ?: findCommentAt(offset - 1)
    
}
