package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.DumbAware
import insyncwithfoo.ryecharm.UVIcons
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.custom.UVCustomTaskFactory


internal class UVRunConfigurationType : ConfigurationType, DumbAware {
    
    override fun getDisplayName() =
        message("runConfigurations.type.displayName")
    
    override fun getConfigurationTypeDescription() =
        message("runConfigurations.type.description")
    
    override fun getIcon() = UVIcons.TINY_16
    
    override fun getId() = UVRunIDs.MAIN_TYPE
    
    override fun getConfigurationFactories() =
        arrayOf(UVCustomTaskFactory(this))
    
}
