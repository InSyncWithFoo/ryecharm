package insyncwithfoo.ryecharm.uv.run.projectscripts

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.PanelBasedSettingsEditor
import insyncwithfoo.ryecharm.uv.run.argumentsInput
import insyncwithfoo.ryecharm.uv.run.commandInfoInputs
import insyncwithfoo.ryecharm.uv.run.experimentalFeatureDisclaimer


internal class UVProjectScriptSettingsEditor(settings: UVProjectScriptSettings, project: Project) :
    PanelBasedSettingsEditor<UVProjectScriptSettings, UVProjectScript>(settings, project)
{
    
    override val panel by lazy { makeComponent() }
    
}


private fun Row.scriptNameInput(block: Cell<JBTextField>.() -> Unit) =
    textField().makeFlexible().apply(block)


private fun UVProjectScriptSettingsEditor.makeComponent() = panel {
    
    experimentalFeatureDisclaimer()
    
    separator()
    
    row(message("runConfigurations.settings.scriptName.label")) {
        scriptNameInput { bindText(settings::scriptName) }
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
