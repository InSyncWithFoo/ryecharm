package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationOrList
import insyncwithfoo.ryecharm.ruff.documentation.providers.NoqaCodeDocumentationTargetProvider
import insyncwithfoo.ryecharm.toDocumentationResult


/**
 * @see NoqaCodeDocumentationTargetProvider
 */
internal class NoqaCodeDocumentationTarget(
    override val element: PsiElement,
    private val noqaComment: NoqaComment,
    private val offset: Int
) : RuffDocumentationTarget() {
    
    override fun fromDereferenced(element: PsiElement) =
        NoqaCodeDocumentationTarget(element, noqaComment, offset)
    
    override fun computeDocumentation(): DocumentationResult? {
        val ruleCode = noqaComment.findCodeAtOffset(offset) ?: return null
        
        return DocumentationResult.asyncDocumentation {
            project.getRuleDocumentationOrList(ruleCode)?.toDocumentationResult()
        }
    }
    
}
