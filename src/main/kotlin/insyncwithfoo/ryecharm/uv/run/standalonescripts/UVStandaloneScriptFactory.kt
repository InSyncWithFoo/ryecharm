package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationFactory
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationType
import insyncwithfoo.ryecharm.uv.run.UVRunIDs


internal class UVStandaloneScriptFactory(type: UVRunConfigurationType) :
    UVRunConfigurationFactory<UVStandaloneScriptSettings, UVStandaloneScript>(type) {
    
    override fun getId() = UVRunIDs.STANDALONE_SCRIPT_FACTORY
    
    override fun getName() = message("runConfigurations.factory.standaloneScript.name")
    
    override fun getOptionsClass() =
        UVStandaloneScriptSettings::class.java
    
    override fun createConfiguration(name: String, project: Project) =
        UVStandaloneScript(name, project, factory = this)
    
}
