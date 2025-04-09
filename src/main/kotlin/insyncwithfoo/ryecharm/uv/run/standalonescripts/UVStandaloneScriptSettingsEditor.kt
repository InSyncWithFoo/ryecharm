package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.singleFileTextField
import insyncwithfoo.ryecharm.uv.run.PanelBasedSettingsEditor
import insyncwithfoo.ryecharm.uv.run.argumentsInput
import insyncwithfoo.ryecharm.uv.run.commandInfoInputs
import insyncwithfoo.ryecharm.uv.run.experimentalFeatureDisclaimer


internal class UVStandaloneScriptSettingsEditor(settings: UVStandaloneScriptSettings, project: Project) :
    PanelBasedSettingsEditor<UVStandaloneScriptSettings, UVStandaloneScript>(settings, project) {
    
    override val panel by lazy { makeComponent() }
    
}


private fun Row.scriptPathInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun UVStandaloneScriptSettingsEditor.makeComponent() = panel {
    
    experimentalFeatureDisclaimer()
    
    separator()
    
    row(message("runConfigurations.settings.scriptPath.label")) {
        scriptPathInput { bindText(settings::scriptPath) }
    }
    
    row(message("runConfigurations.settings.scriptArguments.label")) {
        argumentsInput(settings::scriptArguments)
    }
    
    separator()
    
    commandInfoInputs(project, settings) {
        row(message("runConfigurations.settings.extraArguments.label")) {
            argumentsInput(settings::extraArguments)
        }
    }
    
}
