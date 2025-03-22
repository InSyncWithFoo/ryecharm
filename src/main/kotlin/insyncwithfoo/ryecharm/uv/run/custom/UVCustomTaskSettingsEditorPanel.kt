package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.singleFolderTextField
import kotlin.reflect.KMutableProperty0


private typealias EnvironmentVariablesEditor = EnvironmentVariablesTextFieldWithBrowseButton
private typealias EnvironmentVariables = MutableMap<String, String>


private fun <C : RawCommandLineEditor> Cell<C>.bindText(property: KMutableProperty0<String?>): Cell<C> {
    val getter: (C) -> String? = { component -> component.text }
    val setter: (C, String?) -> Unit = { component, value -> component.text = value }
    
    return bind(getter, setter, property.toMutableProperty())
}


private fun <C : EnvironmentVariablesEditor> Cell<C>.bind(property: KMutableProperty0<EnvironmentVariables>): Cell<C> {
    val getter: (C) -> EnvironmentVariables = { component -> component.envs }
    val setter: (C, EnvironmentVariables) -> Unit = { component, value -> component.envs = value }
    
    return bind(getter, setter, property.toMutableProperty())
}


private fun Row.argumentsInput(block: Cell<RawCommandLineEditor>.() -> Unit) =
    cell(RawCommandLineEditor()).makeFlexible().apply(block)


private fun Row.workingDirectoryInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFolderTextField().makeFlexible().apply(block)


private fun Row.environmentVariablesInput(block: Cell<EnvironmentVariablesTextFieldWithBrowseButton>.() -> Unit) =
    cell(EnvironmentVariablesTextFieldWithBrowseButton()).makeFlexible().apply(block)


internal fun UVCustomTaskSettingsEditor.makeComponent() = panel {
    
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
