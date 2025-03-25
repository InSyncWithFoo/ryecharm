package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.UVIcons


/**
 * The base class from which concrete [ConfigurationFactory] classes derive.
 * 
 * A factory is instantiated via [UVRunConfigurationType.getConfigurationFactories].
 * It is responsible for creating new run configurations.
 */
internal abstract class UVRunConfigurationFactory<S : CopyableRunConfigurationSettings, C : UVRunConfiguration<S>>(
    type: UVRunConfigurationType
) : ConfigurationFactory(type) {
    
    abstract override fun getId(): String
    abstract override fun getName(): String
    abstract override fun getOptionsClass(): Class<S>
    
    /**
     * Create a new run configuration
     * with the given [name] and [project].
     * 
     * This is called from [createTemplateConfiguration]
     * and an overload below.
     */
    abstract fun createConfiguration(name: String, project: Project): C
    
    final override fun getIcon() = UVIcons.TINY_16
    final override fun isEditableInDumbMode() = true
    
    /**
     * Create a new run configuration instance from [template].
     *
     * [template] could be a run configuration saved during previous IDE sessions.
     * The default implementation simply passes it back to the caller.
     * It is unknown why this is the case.
     */
    final override fun createConfiguration(name: String?, template: RunConfiguration) =
        createConfiguration(name.orEmpty(), template.project)
    
    final override fun createTemplateConfiguration(project: Project) =
        createConfiguration(name = "", project)
    
}
