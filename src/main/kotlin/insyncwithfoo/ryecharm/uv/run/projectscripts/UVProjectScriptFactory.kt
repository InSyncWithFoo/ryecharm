package insyncwithfoo.ryecharm.uv.run.projectscripts

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationFactory
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationType
import insyncwithfoo.ryecharm.uv.run.UVRunIDs


internal class UVProjectScriptFactory(type: UVRunConfigurationType) :
    UVRunConfigurationFactory<UVProjectScriptSettings, UVProjectScript>(type)
{
    
    override fun getId() = UVRunIDs.PROJECT_SCRIPT_FACTORY
    
    override fun getName() = message("runConfigurations.factory.projectScript.name")
    
    override fun getOptionsClass() =
        UVProjectScriptSettings::class.java
    
    override fun createConfiguration(name: String, project: Project) =
        UVProjectScript(name, project, factory = this)
    
}
