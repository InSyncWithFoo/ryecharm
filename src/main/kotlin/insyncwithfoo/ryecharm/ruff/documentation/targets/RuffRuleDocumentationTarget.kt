package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelectorOrName
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationOrList
import insyncwithfoo.ryecharm.ruff.documentation.providers.RuffRuleDocumentationTargetProvider
import insyncwithfoo.ryecharm.toDocumentationResult


/**
 * @see RuffRuleDocumentationTargetProvider
 */
internal class RuffRuleDocumentationTarget(
    override val element: PsiElement,
    private val selectorOrName: RuleSelectorOrName
) : RuffDocumentationTarget() {
    
    override fun fromDereferenced(element: PsiElement) =
        RuffRuleDocumentationTarget(element, selectorOrName)
    
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        element.project.getRuleDocumentationOrList(selectorOrName)?.toDocumentationResult()
    }
    
}
