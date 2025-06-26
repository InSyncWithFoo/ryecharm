package insyncwithfoo.ryecharm.configurations.uv

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.fallbackValue
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


private fun Row.showDependencyTreesOnHoverInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.showDependencyTreesOnHover.label")).apply(block)


private fun Row.showVersionSpecifiersForDependenciesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.showVersionSpecifiersForDependencies.label")).apply(block)


private fun Row.showLatestVersionsForDependenciesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.showLatestVersionsForDependencies.label")).apply(block)


private fun Row.dedupeDependencyTreesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.dedupeDependencyTrees.label")).apply(block)


private fun Row.dependencyTreeDepthInput(block: Cell<JBIntSpinner>.() -> Unit) =
    spinner(0..1_000_000).apply(block)


private fun Row.showInvertedDependencyTreeFirstInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.showInvertedDependencyTreeFirst.label")).apply(block)


private fun Row.retrieveDependenciesInReadActionInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.uv.retrieveDependenciesInReadAction.label")).apply(block)


private fun Row.dependenciesDataMaxAgeInput(block: Cell<JBIntSpinner>.() -> Unit) =
    spinner(0..1_000_000).apply(block)


@Suppress("DialogTitleCapitalization")
private fun UVPanel.makeComponent() = panel {
    
    row(message("configurations.uv.executable.label")) {
        executableInput {
            val detectedExecutable = UV.detectExecutable()?.toString()
            
            bindText(state::executable)
            
            fallbackValue = detectedExecutable
            emptyText = detectedExecutable ?: message("configurations.uv.executable.placeholder")
        }
        overrideCheckbox(state::executable)
    }
    
    row(message("configurations.uv.configurationFile.label")) {
        configurationFileInput { bindText(state::configurationFile) }
        overrideCheckbox(state::configurationFile)
    }
    
    group(message("configurations.uv.groups.main")) {
        row {
            showDependencyTreesOnHoverInput { bindSelected(state::showDependencyTreesOnHover) }
            overrideCheckbox(state::showDependencyTreesOnHover)
        }
        indent {
            row {
                showVersionSpecifiersForDependenciesInput { bindSelected(state::showVersionSpecifiersForDependencies) }
                overrideCheckbox(state::showVersionSpecifiersForDependencies)
            }
            row {
                showLatestVersionsForDependenciesInput { bindSelected(state::showLatestVersionsForDependencies) }
                overrideCheckbox(state::showLatestVersionsForDependencies)
            }
            row {
                dedupeDependencyTreesInput { bindSelected(state::dedupeDependencyTrees) }
                overrideCheckbox(state::dedupeDependencyTrees)
            }
            row(message("configurations.uv.dependencyTreeDepth.label")) {
                dependencyTreeDepthInput { bindIntValue(state::dependencyTreeDepth) }
                overrideCheckbox(state::dependencyTreeDepth)
            }
            
            separator()
            
            row {
                showInvertedDependencyTreeFirstInput { bindSelected(state::showInvertedDependencyTreeFirst) }
                overrideCheckbox(state::showInvertedDependencyTreeFirst)
            }
        }
    }
    
    advancedSettingsGroup {
        row {
            retrieveDependenciesInReadActionInput { bindSelected(state::retrieveDependenciesInReadAction) }
            overrideCheckbox(state::retrieveDependenciesInReadAction)
        }
        row(message("configurations.uv.dependenciesDataMaxAge.label")) {
            dependenciesDataMaxAgeInput { bindIntValue(state::dependenciesDataMaxAge) }
            overrideCheckbox(state::dependenciesDataMaxAge)
        }
    }
    
}


internal fun PanelBasedConfigurable<UVConfigurations>.createPanel(state: UVConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return UVPanel(state, overrides, project).makeComponent()
}
