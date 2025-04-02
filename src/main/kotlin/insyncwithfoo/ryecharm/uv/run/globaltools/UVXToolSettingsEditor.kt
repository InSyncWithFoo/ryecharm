package insyncwithfoo.ryecharm.uv.run.globaltools

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.topLabel
import insyncwithfoo.ryecharm.uv.run.PanelBasedSettingsEditor
import insyncwithfoo.ryecharm.uv.run.argumentsInput
import insyncwithfoo.ryecharm.uv.run.commandInfoInputs
import insyncwithfoo.ryecharm.uv.run.experimentalFeatureDisclaimer


internal class UVXToolSettingsEditor(settings: UVXToolSettings, project: Project) :
    PanelBasedSettingsEditor<UVXToolSettings, UVXTool>(settings, project) {
    
    override val panel by lazy { makeComponent() }
    
}


private fun Row.toolNameInput(block: Cell<JBTextField>.() -> Unit) =
    textField().topLabel(message("runConfigurations.settings.toolName.label")).makeFlexible().apply(block)


private fun Row.fromPackageInput(block: Cell<JBTextField>.() -> Unit) = 
    textField().topLabel(message("runConfigurations.settings.fromPackage.label")).makeFlexible().apply(block)


private fun UVXToolSettingsEditor.makeComponent() = panel {
    
    experimentalFeatureDisclaimer()
    
    separator()
    
    row {
        toolNameInput { bindText(settings::toolName) }
        fromPackageInput { bindText(settings::fromPackage) }
    }
    
    row(message("runConfigurations.settings.toolArguments.label")) {
        argumentsInput(settings::toolArguments)
    }
    
    separator()
    
    commandInfoInputs(project, settings) {
        row(message("runConfigurations.settings.extraArguments.label")) {
            argumentsInput(settings::extraArguments)
        }
    }
    
}
