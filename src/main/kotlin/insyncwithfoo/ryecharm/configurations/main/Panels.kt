package insyncwithfoo.ryecharm.configurations.main

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.message


private class MainPanel(state: MainConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<MainConfigurations>(state, overrides, project)


private fun Row.languageInjectionPEP723BlocksInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.languageInjectionPEP723Blocks.label")).apply(block)


private fun Row.languageInjectionRequirementsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.languageInjectionRequirements.label")).apply(block)


private fun Row.suppressIncorrectNIRIInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.suppressIncorrectNIRI.label")).apply(block)


private fun Row.suppressIncorrectNIRINonUVSDKInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.suppressIncorrectNIRINonUVSDK.label")).apply(block)


private fun Row.consoleFilterRuffAndTYPathsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.consoleFilterRuffAndTYPaths.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun MainPanel.makeComponent() = panel {
    group(message("configurations.main.groups.languageInjection")) {
        row {
            languageInjectionPEP723BlocksInput { bindSelected(state::languageInjectionPEP723Blocks) }
            overrideCheckbox(state::languageInjectionPEP723Blocks)
        }
        row {
            languageInjectionRequirementsInput { bindSelected(state::languageInjectionRequirements) }
            overrideCheckbox(state::languageInjectionRequirements)
        }
    }
    
    group(message("configurations.main.groups.inspections")) {
        row {
            suppressIncorrectNIRIInput { bindSelected(state::suppressIncorrectNIRI) }
            overrideCheckbox(state::suppressIncorrectNIRI)
        }
        indent {
            row {
                suppressIncorrectNIRINonUVSDKInput { bindSelected(state::suppressIncorrectNIRINonUVSDK) }
                overrideCheckbox(state::suppressIncorrectNIRINonUVSDK)
            }
        }
    }
    
    group(message("configurations.main.groups.consoleFilters")) {
        row {
            consoleFilterRuffAndTYPathsInput { bindSelected(state::consoleFilterRuffAndTYPaths) }
            overrideCheckbox(state::consoleFilterRuffAndTYPaths)
        }
    }
}


internal fun PanelBasedConfigurable<MainConfigurations>.createPanel(state: MainConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return MainPanel(state, overrides, project).makeComponent()
}
