package insyncwithfoo.ryecharm.uv.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import insyncwithfoo.ryecharm.configurations.copyTo


/**
 * The base class from which concrete [SettingsEditor] classes derive.
 * 
 * This class implements and delegates [resetEditorFrom] and [applyEditorTo]
 * to that of the panel.
 * 
 * @param settings A new state instance for this panel alone.
 */
internal abstract class PanelBasedSettingsEditor<S : UVRunConfigurationSettings, C : UVRunConfiguration<S>>(
    val settings: S,
    val project: Project
) : SettingsEditor<C>() {
    
    protected abstract val panel: DialogPanel
    
    override fun createEditor() = panel
    
    override fun resetEditorFrom(configuration: C) {
        configuration.settings.copyTo(settings)
        panel.reset()
    }
    
    /**
     * Called very frequently on an empty [configuration]
     * to detect whether any changes have been made.
     */
    override fun applyEditorTo(configuration: C) {
        panel.apply()
        settings.copyTo(configuration.settings)
    }
    
}
