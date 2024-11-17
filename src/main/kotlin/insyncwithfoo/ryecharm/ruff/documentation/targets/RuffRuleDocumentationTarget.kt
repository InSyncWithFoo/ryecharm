package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.getDocumentationForRule
import insyncwithfoo.ryecharm.ruff.documentation.providers.RuffRuleDocumentationTargetProvider
import insyncwithfoo.ryecharm.toDocumentationResult


/**
 * @see RuffRuleDocumentationTargetProvider
 */
internal class RuffRuleDocumentationTarget(
    override val element: PsiElement,
    private val rule: RuleCode
) : RuffDocumentationTarget() {
    
    override fun fromDereferenced(element: PsiElement) =
        RuffRuleDocumentationTarget(element, rule)
    
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        element.project.getDocumentationForRule(rule)?.toDocumentationResult()
    }
    
}
