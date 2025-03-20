package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelectorOrName
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationByFullCode
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationByRuleName
import insyncwithfoo.ryecharm.ruff.documentation.getRuleListBySelector
import insyncwithfoo.ryecharm.ruff.documentation.isPylintCodePrefix
import insyncwithfoo.ryecharm.ruff.documentation.providers.RuffRuleDocumentationTargetProvider
import insyncwithfoo.ryecharm.ruff.documentation.ruleSelector
import insyncwithfoo.ryecharm.toDocumentationResult
import insyncwithfoo.ryecharm.toHTMLInReadAction


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
        project.getMarkdownDocumentation()
            ?.toHTMLInReadAction()
            ?.toDocumentationResult()
    }
    
    private suspend fun Project.getMarkdownDocumentation(): Markdown? {
        val match = ruleSelector.matchEntire(selectorOrName)
            ?: return getRuleDocumentationByRuleName(selectorOrName)
        
        val (linter, number) = match.destructured
        
        val selectorIsPrefix = when (linter.isPylintCodePrefix) {
            true -> number.length != 4
            else -> number.length != 3
        }
        
        return when (selectorIsPrefix) {
            true -> getRuleListBySelector(selectorOrName)
            else -> getRuleDocumentationByFullCode(selectorOrName) ?: getRuleListBySelector(selectorOrName)
        }
    }
    
}
