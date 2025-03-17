package insyncwithfoo.ryecharm.ruff.documentation.providers

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.mayContainRuffOptions
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffRuleDocumentationTarget
import insyncwithfoo.ryecharm.ruff.extractRuleSelector
import insyncwithfoo.ryecharm.wrappingTomlLiteral
import org.toml.lang.TomlLanguage


/**
 * Provide documentation for rules specified in
 * the following selector arrays:
 * 
 * * (`lint`) `extend-fixable`
 * * (`lint`) `extend-ignore` (deprecated)
 * * (`lint`) `extend-per-file-ignores.*`
 * * (`lint`) `extend-safe-fixes`
 * * (`lint`) `extend-select`
 * * (`lint`) `extend-unfixable`
 * * (`lint`) `extend-unsafe-fixes`
 * * (`lint`) `fixable`
 * * (`lint`) `ignore`
 * * (`lint`) `per-file-ignores.*`
 * * (`lint`) `select`
 * * (`lint`) `unfixable`
 * 
 * The top-level settings as well as `extend-ignore`
 * are deprecated and should be removed
 * once they are no longer recognized by Ruff itself.
 */
internal class RuffRuleDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int) =
        listOfNotNull(documentationTarget(file, offset))
    
    private fun documentationTarget(file: PsiFile, offset: Int): DocumentationTarget? {
        val project = file.project
        val configurations = project.ruffConfigurations
        val virtualFile = file.virtualFile ?: return null
        
        when {
            !configurations.documentationPopups -> return null
            !configurations.documentationPopupsForTOMLRuleCodes -> return null
            !file.language.isKindOf(TomlLanguage) -> return null
            !virtualFile.mayContainRuffOptions -> return null
        }
        
        val element = file.findElementAt(offset) ?: return null
        val string = element.wrappingTomlLiteral ?: return null
        
        val selector = string.extractRuleSelector(virtualFile) ?: return null
        
        return RuffRuleDocumentationTarget(string, selector)
    }
    
}
