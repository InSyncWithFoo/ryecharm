package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.DumbAware
import insyncwithfoo.ryecharm.UVIcons
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.custom.UVCustomTaskFactory


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-79031
/**
 * The umbrella type of all uv run configurations.
 * 
 * As with other types, this is displayed as an item
 * within the "add" list of the run configuration dialog.
 */
internal class UVRunConfigurationType : ConfigurationType, DumbAware {
    
    /**
     * The name to be díplayed
     * in the run configuration dialog.
     */
    override fun getDisplayName() =
        message("runConfigurations.type.displayName")
    
    /**
     * A description to be displayed in some UI contexts.
     * 
     * Less visible than the display name.
     */
    override fun getConfigurationTypeDescription() =
        message("runConfigurations.type.description")
    
    /**
     * The icon to be displayed
     * in the run configuration dialog.
     */
    override fun getIcon() = UVIcons.TINY_16
    
    /**
     * The ID to be used during (de)serialization
     * of run configuration instances.
     * 
     * Must not change under any circumstances.
     */
    override fun getId() = UVRunIDs.MAIN_TYPE
    
    /**
     * The factories by which the user can create
     * run configuration instances of this type.
     * 
     * Each factory corresponds to a subpanel
     * in the run configuration dialog.
     */
    override fun getConfigurationFactories() =
        arrayOf(UVCustomTaskFactory(this))
    
}
