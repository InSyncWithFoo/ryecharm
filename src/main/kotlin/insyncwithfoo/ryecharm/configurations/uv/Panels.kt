package insyncwithfoo.ryecharm.configurations.uv

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.singleFileTextField
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.detectExecutable


private class UVPanel(state: UVConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<UVConfigurations>(state, overrides, project)


private fun Row.executableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.configurationFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.packageManagingInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.packageManaging.label")).apply(block)


private fun Row.packageManagingNonUVProjectInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.packageManagingNonUVProject.label")).apply(block)


private fun Row.retrieveDependenciesInReadActionInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.retrieveDependenciesInReadAction.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun UVPanel.makeComponent() = panel {
    
    row(message("configurations.uv.executable.label")) {
        executableInput {
            val detectedExecutable = UV.detectExecutable()?.toString()
            
            bindText(state::executable) { detectedExecutable.orEmpty() }
            emptyText = detectedExecutable ?: message("configurations.uv.executable.placeholder")
        }
        overrideCheckbox(state::executable)
    }
    
    row(message("configurations.uv.configurationFile.label")) {
        configurationFileInput { bindText(state::configurationFile) }
        overrideCheckbox(state::configurationFile)
    }
    
    group(message("configurations.uv.groups.packageManagement")) {
        row {
            packageManagingInput { bindSelected(state::packageManaging) }
            overrideCheckbox(state::packageManaging)
        }
        indent {
            row {
                packageManagingNonUVProjectInput { bindSelected(state::packageManagingNonUVProject) }
                overrideCheckbox(state::packageManagingNonUVProject)
            }
        }
    }
    
    advancedSettingsGroup {
        row {
            retrieveDependenciesInReadActionInput { bindSelected(state::retrieveDependenciesInReadAction) }
            overrideCheckbox(state::retrieveDependenciesInReadAction)
        }
    }
    
    timeoutGroup(state.timeouts, UVTimeouts.entries)
    
}


internal fun PanelBasedConfigurable<UVConfigurations>.createPanel(state: UVConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return UVPanel(state, overrides, project).makeComponent()
}
