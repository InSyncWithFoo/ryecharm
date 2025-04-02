package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.PanelBasedSettingsEditor
import insyncwithfoo.ryecharm.uv.run.argumentsInput
import insyncwithfoo.ryecharm.uv.run.commandInfoInputs


internal class UVCustomTaskSettingsEditor(settings: UVCustomTaskSettings, project: Project) :
    PanelBasedSettingsEditor<UVCustomTaskSettings, UVCustomTask>(settings, project) {
    
    override val panel by lazy { makeComponent() }
    
}


private fun UVCustomTaskSettingsEditor.makeComponent() = panel {
    commandInfoInputs(project, settings) {
        row(message("runConfigurations.settings.arguments.label")) {
            argumentsInput(settings::arguments)
        }
    }
}
