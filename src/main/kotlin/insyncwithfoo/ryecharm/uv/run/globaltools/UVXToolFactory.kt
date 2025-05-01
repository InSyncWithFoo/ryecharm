package insyncwithfoo.ryecharm.uv.run.globaltools

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationFactory
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationType
import insyncwithfoo.ryecharm.uv.run.UVRunIDs


internal class UVXToolFactory(type: UVRunConfigurationType) :
    UVRunConfigurationFactory<UVXToolSettings, UVXTool>(type)
{
    
    override fun getId() = UVRunIDs.GLOBAL_TOOL_FACTORY
    
    override fun getName() = message("runConfigurations.factory.globalTool.name")
    
    override fun getOptionsClass() =
        UVXToolSettings::class.java
    
    override fun createConfiguration(name: String, project: Project) =
        UVXTool(name, project, factory = this)
    
}
