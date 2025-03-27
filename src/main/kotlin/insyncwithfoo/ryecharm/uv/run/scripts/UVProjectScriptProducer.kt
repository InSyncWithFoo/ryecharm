package insyncwithfoo.ryecharm.uv.run.scripts

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.uvRunConfigurationFactories
import insyncwithfoo.ryecharm.wrappingTomlKey
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.ext.name


/**
 * Show a "Run" action that runs the corresponding project script
 * when a subkey of `project.scripts` is right-clicked.
 */
internal class UVProjectScriptProducer : LazyRunConfigurationProducer<UVProjectScript>(), DumbAware {
    
    override fun getConfigurationFactory() =
        uvRunConfigurationFactories[1] as UVProjectScriptFactory
    
    override fun isConfigurationFromContext(configuration: UVProjectScript, context: ConfigurationContext): Boolean {
        val location = context.location ?: return false
        val key = location.psiElement.wrappingTomlKey ?: return false
        val scriptName = key.projectScriptName ?: return false
        
        return configuration.settings.script == scriptName
    } 
    
    override fun setupConfigurationFromContext(
        configuration: UVProjectScript,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val key = sourceElement.get()?.wrappingTomlKey ?: return false
        val scriptName = key.projectScriptName ?: return false
        
        configuration.apply {
            name = message("runConfigurations.instance.projectScript.nameTemplate", scriptName)
            settings.script = scriptName
        }
        
        return true
    }
    
    private val TomlKey.projectScriptName: String?
        get() {
            val file = containingFile as? TomlFile ?: return null
            
            when {
                file.virtualFile?.isPyprojectToml != true -> return null
                !(absoluteName isChildOf "project.scripts") -> return null
            }
            
            return name
        }
    
}
