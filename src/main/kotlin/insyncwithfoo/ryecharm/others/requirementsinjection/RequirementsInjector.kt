package insyncwithfoo.ryecharm.others.requirementsinjection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.psi.PsiElement
import com.jetbrains.python.requirements.RequirementsLanguage
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.isPairedWithDependencySpecifierArray
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.DependencyVersionInlayHintsProvider
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlLiteral


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-71120
/**
 * Inject *Requirements* fragments for known arrays of PEP 508 specifiers.
 * 
 * @see isPairedWithDependencySpecifierArray
 * @see DependencyVersionInlayHintsProvider
 */
internal class RequirementsInjector : LanguageInjectionContributor {
    
    override fun getInjection(context: PsiElement): Injection? {
        val project = context.project
        val configurations = project.mainConfigurations
        
        if (!configurations.languageInjectionRequirements) {
            return null
        }
        
        val literal = context as? TomlLiteral ?: return null
        val string = literal.takeIf { it.isString } ?: return null
        val array = string.parent as? TomlArray ?: return null
        val key = array.keyValuePair?.key ?: return null
        
        if (!key.isPairedWithDependencySpecifierArray) {
            return null
        }
        
        val (prefix, suffix) = Pair("", "")
        val supportId = null
        
        return SimpleInjection(RequirementsLanguage.INSTANCE, prefix, suffix, supportId)
    }
    
}
