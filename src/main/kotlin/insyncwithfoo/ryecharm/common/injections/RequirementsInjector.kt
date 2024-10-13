package insyncwithfoo.ryecharm.common.injections

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.psi.PsiElement
import com.jetbrains.python.requirements.RequirementsLanguage
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.keyValuePair
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlLiteral


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-71120
/**
 * Inject *Requirements* fragments for:
 * 
 * * `uv.constraint-dependencies`
 * * `uv.dev-dependencies`
 * * `uv.override-dependencies`
 * * `uv.upgrade-package`
 * * `uv.pip.upgrade-package`
 * 
 * As a bonus, `pyproject.toml`'s `project.optional-dependencies`
 * and `dependency-groups` (PEP 735) are also supported.
 */
internal class RequirementsInjector : LanguageInjectionContributor {
    
    private val injection: Injection
        get() {
            val (prefix, suffix) = Pair("", "")
            val supportId = null
            
            return SimpleInjection(RequirementsLanguage.INSTANCE, prefix, suffix, supportId)
        }
    
    override fun getInjection(context: PsiElement): Injection? {
        val project = context.project
        val configurations = project.mainConfigurations
        
        if (!configurations.languageInjectionRequirements) {
            return null
        }
        
        val literal = context as? TomlLiteral ?: return null
        val array = literal.parent as? TomlArray ?: return null
        val keyValuePair = array.keyValuePair ?: return null
        
        if (!literal.isString) {
            return null
        }
        
        return getInjectionGivenKey(keyValuePair.key)
    }
    
    private fun getInjectionGivenKey(key: TomlKey): Injection? {
        val file = key.containingFile ?: return null
        val virtualFile = file.virtualFile ?: return null
        val absoluteName = key.absoluteName
        
        val nonUVRequirementPropertyParents = listOf("project.optional-dependencies", "dependency-groups")
        
        if (virtualFile.isPyprojectToml && nonUVRequirementPropertyParents.any { absoluteName isChildOf it }) {
            return injection
        }
        
        val relativeName = when {
            virtualFile.isPyprojectToml -> absoluteName.relativize("tool.uv") ?: return null
            virtualFile.isUVToml -> absoluteName
            else -> return null
        }
        
        return getInjectionGivenUVPropertyName(relativeName)
    }
    
    private fun getInjectionGivenUVPropertyName(keyRelativeName: TOMLPath): Injection? {
        val requirementsKeyNames = TOMLPath.listOf(
            "constraint-dependencies",
            "dev-dependencies",
            "override-dependencies",
            "upgrade-package",
            "pip.upgrade-package"
        )
        
        if (keyRelativeName !in requirementsKeyNames) {
            return null
        }
        
        return injection
    }
    
}
