package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationFactory
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationType
import insyncwithfoo.ryecharm.uv.run.UVRunIDs


internal class UVCustomTaskFactory(type: UVRunConfigurationType) :
    UVRunConfigurationFactory<UVCustomTaskSettings, UVCustomTask>(type) {
    
    override fun getId() = UVRunIDs.CUSTOM_TASK_FACTORY
    
    override fun getName() = message("runConfigurations.factory.customTask.name")
    
    override fun getOptionsClass() =
        UVCustomTaskSettings::class.java
    
    override fun createConfiguration(name: String, project: Project) =
        UVCustomTask(name, project, factory = this)
    
}
