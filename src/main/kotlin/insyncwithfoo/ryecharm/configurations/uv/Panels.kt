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


private fun Row.makeExecutableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.makeConfigurationFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.makePackageManagingUVProjectsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.packageManagingUVProjects.label")).apply(block)


private fun Row.makePackageManagingNonUVProjectsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.packageManagingNonUVProjects.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun UVPanel.makeComponent() = panel {
    
    row(message("configurations.uv.executable.label")) {
        makeExecutableInput {
            val detectedExecutable = UV.detectExecutable()?.toString()
            
            bindText(state::executable) { detectedExecutable.orEmpty() }
            emptyText = detectedExecutable ?: message("configurations.uv.executable.placeholder")
        }
        makeOverrideCheckboxIfApplicable(state::executable)
    }
    
    row(message("configurations.uv.configurationFile.label")) {
        makeConfigurationFileInput { bindText(state::configurationFile) }
        makeOverrideCheckboxIfApplicable(state::configurationFile)
    }
    
    group(message("configurations.uv.groups.packageManagement")) {
        row {
            label(message("configurations.uv.subgroups.packageManagementOperations.groupLabel"))
        }
        indent {
            row {
                makePackageManagingUVProjectsInput { bindSelected(state::packageManagingUVProjects) }
                makeOverrideCheckboxIfApplicable(state::packageManagingUVProjects)
            }
            row {
                makePackageManagingNonUVProjectsInput { bindSelected(state::packageManagingNonUVProjects) }
                makeOverrideCheckboxIfApplicable(state::packageManagingNonUVProjects)
            }
        }
    }
    
    makeTimeoutGroup(state.timeouts, UVTimeouts.entries)
    
}


internal fun PanelBasedConfigurable<UVConfigurations>.createPanel(state: UVConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return UVPanel(state, overrides, project).makeComponent()
}
