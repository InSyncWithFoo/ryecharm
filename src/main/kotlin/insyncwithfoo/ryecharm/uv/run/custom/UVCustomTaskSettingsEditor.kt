package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copyTo


internal class UVCustomTaskSettingsEditor(val settings: UVCustomTaskSettings, val project: Project) :
    SettingsEditor<UVCustomTask>() {
    
    private val panel by lazy { makeComponent() }
    
    override fun resetEditorFrom(configuration: UVCustomTask) {
        configuration.settings.copyTo(settings)
        panel.reset()
    }
    
    override fun applyEditorTo(configuration: UVCustomTask) {
        panel.apply()
        settings.copyTo(configuration.settings)
    }
    
    override fun createEditor() = panel
    
}
