package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.singleFolderTextField
import insyncwithfoo.ryecharm.uv.run.PanelBasedSettingsEditor
import insyncwithfoo.ryecharm.uv.run.bind
import insyncwithfoo.ryecharm.uv.run.bindText


internal class UVCustomTaskSettingsEditor(settings: UVCustomTaskSettings, project: Project) :
    PanelBasedSettingsEditor<UVCustomTaskSettings, UVCustomTask>(settings, project) {
    
    override val panel by lazy { makeComponent() }
    
}


private fun Row.argumentsInput(block: Cell<RawCommandLineEditor>.() -> Unit) =
    cell(RawCommandLineEditor()).makeFlexible().apply(block)


private fun Row.workingDirectoryInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFolderTextField().makeFlexible().apply(block)


private fun Row.environmentVariablesInput(block: Cell<EnvironmentVariablesTextFieldWithBrowseButton>.() -> Unit) =
    cell(EnvironmentVariablesTextFieldWithBrowseButton()).makeFlexible().apply(block)


private fun UVCustomTaskSettingsEditor.makeComponent() = panel {
    
    row(message("runConfigurations.settings.arguments.label")) {
        argumentsInput { bindText(settings::arguments) }
    }
    
    row(message("runConfigurations.settings.workingDirectory.label")) {
        // TODO: Support macros
        workingDirectoryInput {
            val projectPath = project.path?.toString().orEmpty()
            
            bindText(settings::workingDirectory) { projectPath }
            emptyText = projectPath
        }
    }
    
    row(message("runConfigurations.settings.environmentVariables.label")) {
        environmentVariablesInput { bind(settings::environmentVariables) }
    }
    
}
