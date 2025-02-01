package insyncwithfoo.ryecharm.ruff.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import insyncwithfoo.ryecharm.ExternalIntentionAction
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.replaceString
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.RuleCode


internal class ReenableRule : ExternalIntentionAction {
    
    private var code: RuleCode? = null
    
    override fun startInWriteAction() = true
    
    override fun getFamilyName() = message("intentions.ruff.reenableRule.familyName")
    
    override fun getText() = message("intentions.ruff.reenableRule.name", code.toString())
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val offset = editor?.caretModel?.offset ?: return false
        val comment = file?.findElementAt(offset) as? PsiComment ?: return false
        
        val noqaComment = NoqaComment.parse(comment) ?: return false
        
        code = noqaComment.findCodeAtOffset(offset) ?: return false
        
        return true
    }
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val offset = editor?.caretModel?.offset ?: return
        val element = file?.findElementAt(offset) as? PsiComment ?: return
        
        val noqaComment = NoqaComment.parse(element) ?: return
        val code = noqaComment.findCodeAtOffset(offset) ?: return
        val newContent = noqaComment.withoutCode(code)
        
        if (newContent.isNotEmpty() || noqaComment.start != element.startOffset) {
            file.edit { it.replaceString(noqaComment.range, newContent) }
            return
        }
        
        val restRange = noqaComment.toString().length..<element.textLength
        val restTrimmed = element.text.slice(restRange).trimStart()
        
        when (restTrimmed.startsWith("#")) {
            true -> file.edit { it.replaceString(element.textRange, restTrimmed) }
            else -> file.edit { it.replaceString(noqaComment.range, "#") }
        }
    }
    
}
