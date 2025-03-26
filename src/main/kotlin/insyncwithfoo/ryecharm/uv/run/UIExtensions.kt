package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.singleFolderTextField
import kotlin.reflect.KMutableProperty0


internal typealias EnvironmentVariablesEditor = EnvironmentVariablesTextFieldWithBrowseButton


private fun Row.argumentsInput(block: Cell<RawCommandLineEditor>.() -> Unit) =
    cell(RawCommandLineEditor()).makeFlexible().apply(block)


internal fun Row.argumentsInput(property: KMutableProperty0<String?>) =
    argumentsInput { bindText(property) }


private fun Row.workingDirectoryInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFolderTextField().makeFlexible().apply(block)


// TODO: Support macros
internal fun Row.workingDirectoryInput(project: Project, property: KMutableProperty0<String?>) =
    workingDirectoryInput {
        val projectPath = project.path?.toString().orEmpty()
        
        bindText(property) { projectPath }
        emptyText = projectPath
    }


private fun Row.environmentVariablesInput(block: Cell<EnvironmentVariablesEditor>.() -> Unit) =
    cell(EnvironmentVariablesEditor()).makeFlexible().apply(block)


internal fun Row.environmentVariablesInput(property: KMutableProperty0<EnvironmentVariables>) =
    environmentVariablesInput { bind(property) }
