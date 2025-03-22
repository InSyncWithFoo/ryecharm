package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.UVIcons
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationType
import insyncwithfoo.ryecharm.uv.run.UVRunIDs


internal class UVCustomTaskFactory(type: UVRunConfigurationType) : ConfigurationFactory(type) {
    
    override fun getId() = UVRunIDs.CUSTOM_FACTORY
    
    override fun getIcon() = UVIcons.TINY_16
    
    override fun isEditableInDumbMode() = true
    
    override fun getOptionsClass() =
        UVCustomTaskSettings::class.java
    
    override fun createConfiguration(name: String?, template: RunConfiguration) =
        UVCustomTask(name.orEmpty(), template.project, factory = this)
    
    override fun createTemplateConfiguration(project: Project) =
        UVCustomTask(name = "", project, factory = this)
    
}
