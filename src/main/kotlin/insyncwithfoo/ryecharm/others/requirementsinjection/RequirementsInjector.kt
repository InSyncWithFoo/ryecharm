package insyncwithfoo.ryecharm.others.requirementsinjection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.requirements.RequirementsLanguage
import com.jetbrains.python.requirements.injection.TomlRequirementsLanguageInjector
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.isDependencySpecifierString
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.DependencyVersionInlayHintsProvider


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-71120
// https://github.com/JetBrains/intellij-community/blob/b653610871/python/src/com/jetbrains/python/requirements/injection/TomlRequirementsInjectionSupport.kt
/**
 * Inject *Requirements* fragments for known arrays of PEP 508 specifiers.
 * 
 * Since 2025.2, this class partially overlaps [TomlRequirementsLanguageInjector]
 * with regard to:
 * 
 * * `project.optional-dependencies.*`
 * * `dependency-groups.*`
 * * `build-system.requires`
 * * `tool.uv.dev-dependencies`
 * 
 * @see isDependencySpecifierString
 * @see DependencyVersionInlayHintsProvider
 */
internal class RequirementsInjector : LanguageInjectionContributor, DumbAware {
    
    override fun getInjection(context: PsiElement): Injection? {
        val project = context.project
        val configurations = project.mainConfigurations
        
        if (!configurations.languageInjectionRequirements) {
            return null
        }
        
        if (!context.isDependencySpecifierString) {
            return null
        }
        
        val (prefix, suffix) = Pair("", "")
        val supportId = null
        
        return SimpleInjection(RequirementsLanguage.INSTANCE, prefix, suffix, supportId)
    }
    
}
