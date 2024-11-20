package insyncwithfoo.ryecharm.ruff.documentation.providers

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.documentation.targets.NoqaCodeDocumentationTarget


/**
 * Provide documentation for rule codes in `# noqa` comments:
 * 
 * ```python
 * # ruff: noqa: RUF001
 * #             ^ hover: ambiguous-unicode-character-string...
 * 
 * a + b # noqa: RUF001
 *       #       ^ hover: ambiguous-unicode-character-string...
 * ```
 */
internal class NoqaCodeDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> =
        listOfNotNull(documentationTarget(file, offset))
    
    private fun documentationTarget(file: PsiFile, offset: Int): DocumentationTarget? {
        val project = file.project
        val configurations = project.ruffConfigurations
        
        when {
            !configurations.documentationPopups -> return null
            !configurations.documentationPopupsForNoqaComments -> return null
            configurations.runningMode != RunningMode.COMMAND_LINE -> return null
            !file.isSupportedByRuff -> return null
        }
        
        val psiComment = file.findElementAt(offset) as? PsiComment
        
        return psiComment?.toTarget(offset)
    }
    
    private fun PsiComment.toTarget(hoverOffset: Int): DocumentationTarget? {
        val noqaComment = NoqaComment.parse(this) ?: return null
        
        return NoqaCodeDocumentationTarget(this, noqaComment, hoverOffset)
    }
    
}
