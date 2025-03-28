package insyncwithfoo.ryecharm.uv.run.scripts

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.uvRunConfigurationFactories
import insyncwithfoo.ryecharm.wrappingTomlKey


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
    
}
