package insyncwithfoo.ryecharm.ruff.documentation.noqa

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff


internal class NoqaCodeDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> {
        val project = file.project
        val configurations = project.ruffConfigurations
        
        when {
            !file.isSupportedByRuff -> return emptyList()
            !configurations.showDocumentationForNoqaCodes -> return emptyList()
            configurations.runningMode != RunningMode.COMMAND_LINE -> return emptyList()
        }
        
        val psiComment = file.findElementAt(offset) as? PsiComment
        val target = psiComment?.documentationTarget(offset)
        
        return listOfNotNull(target)
    }
    
    private fun PsiComment.documentationTarget(hoverOffset: Int): NoqaCodeDocumentationTarget? {
        val noqaComment = NoqaComment.parse(this) ?: return null
        
        return NoqaCodeDocumentationTarget(this, noqaComment, hoverOffset)
    }
    
}
