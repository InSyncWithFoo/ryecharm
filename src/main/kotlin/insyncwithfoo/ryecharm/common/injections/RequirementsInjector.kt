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
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.keyValuePair
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlElementTypes
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.elementType


private val TomlLiteral.isString: Boolean
    get() = firstChild?.elementType in listOf(
        TomlElementTypes.BASIC_STRING,
        TomlElementTypes.LITERAL_STRING,
        TomlElementTypes.MULTILINE_BASIC_STRING,
        TomlElementTypes.MULTILINE_LITERAL_STRING
    )


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-71120
/**
 * Inject *Requirements* fragments for elements of:
 * 
 * * `uv.constraint-dependencies`
 * * `uv.dev-dependencies`
 * * `uv.override-dependencies`
 * * `uv.upgrade-package`
 * * `uv.pip.upgrade-package`
 * 
 * As a bonus, `pyproject.toml`'s
 * `project.optional-dependencies` is also supported.
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
        
        return getInjection(keyValuePair.key)
    }
    
    private fun getInjection(key: TomlKey): Injection? {
        val file = key.containingFile ?: return null
        val virtualFile = file.virtualFile ?: return null
        val absoluteName = key.absoluteName
        
        if (virtualFile.isPyprojectToml && absoluteName isChildOf TOMLPath("project.optional-dependencies")) {
            return injection
        }
        
        val relativeName = when {
            virtualFile.isPyprojectToml -> absoluteName.relativize(TOMLPath("tool.uv")) ?: return null
            virtualFile.isUVToml -> absoluteName
            else -> return null
        }
        
        return getInjection(relativeName)
    }
    
    private fun getInjection(keyRelativeName: TOMLPath): Injection? {
        val requirementsKeyNames = listOf(
            TOMLPath("constraint-dependencies"),
            TOMLPath("dev-dependencies"),
            TOMLPath("override-dependencies"),
            TOMLPath("upgrade-package"),
            TOMLPath("pip.upgrade-package")
        )
        
        if (keyRelativeName !in requirementsKeyNames) {
            return null
        }
        
        return injection
    }
    
}
