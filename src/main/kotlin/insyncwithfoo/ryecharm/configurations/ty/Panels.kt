package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindItem
import insyncwithfoo.ryecharm.bindSelected
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.comboBox
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.radioButtonForPotentiallyUnavailable
import insyncwithfoo.ryecharm.singleFileTextField
import insyncwithfoo.ryecharm.ty.commands.TY
import insyncwithfoo.ryecharm.ty.commands.detectExecutable


private class TYPanel(state: TYConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<TYConfigurations>(state, overrides, project)


private fun Row.executableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Panel.runningModeInputGroup(block: Panel.() -> Unit) =
    buttonsGroup(init = block)


private fun Row.diagnosticsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.diagnostics.label")).apply(block)


private fun Row.diagnosticModeInput(block: Cell<ComboBox<DiagnosticMode>>.() -> Unit) =
    comboBox<DiagnosticMode>().apply(block)


private fun Row.inlayHintsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.inlayHints.label")).apply(block)


private fun Row.inlayHintsVariableTypesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.inlayHintsVariableTypes.label")).apply(block)


private fun Row.inlayHintsCallArgumentNamesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.inlayHintsCallArgumentNames.label")).apply(block)


private fun Row.experimentalRenameInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.experimentalRename.label")).apply(block)


private fun Row.experimentalAutoImportInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ty.experimentalAutoImport.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun TYPanel.makeComponent() = panel {
    
    row(message("configurations.ty.executable.label")) {
        val detectedExecutable = TY.detectExecutable()?.toString()
        
        executableInputAndDetectButton(detectedExecutable, ::executableInput) { bindText(state::executable) }
        overrideCheckbox(state::executable)
    }
    
    val runningModeInputGroup = runningModeInputGroup {
        row(message("configurations.ty.runningMode.label")) {
            radioButtonFor(RunningMode.DISABLED)
            radioButtonForPotentiallyUnavailable(RunningMode.LSP4IJ) { lsp4ijIsAvailable }
            radioButtonForPotentiallyUnavailable(RunningMode.LSP) { lspIsAvailable }
            
            overrideCheckbox(state::runningMode)
        }
    }
    runningModeInputGroup.bindSelected(state::runningMode)
    
    group(message("configurations.ty.groups.languageServer")) {
        
        row {
            diagnosticsInput { bindSelected(state::diagnostics) }
            overrideCheckbox(state::diagnostics)
        }
        indent {
            row(message("configurations.ty.diagnosticMode.label")) {
                diagnosticModeInput { bindItem(state::diagnosticMode) }
                overrideCheckbox(state::diagnosticMode)
            }
        }
        
        row {
            inlayHintsInput { bindSelected(state::inlayHints) }
            overrideCheckbox(state::inlayHints)
        }
        indent {
            row {
                inlayHintsVariableTypesInput { bindSelected(state::inlayHintsVariableTypes) }
                overrideCheckbox(state::inlayHintsVariableTypes)
            }
            row {
                inlayHintsCallArgumentNamesInput { bindSelected(state::inlayHintsCallArgumentNames) }
                overrideCheckbox(state::inlayHintsCallArgumentNames)
            }
        }
        
        row {
            label(message("configurations.ty.subgroups.experimental"))
        }
        indent {
            row {
                experimentalRenameInput { bindSelected(state::experimentalRename) }
                overrideCheckbox(state::experimentalRename)
            }
            row {
                experimentalAutoImportInput { bindSelected(state::experimentalAutoImport) }
                overrideCheckbox(state::experimentalAutoImport)
            }
        }
        
    }
    
}


internal fun PanelBasedConfigurable<TYConfigurations>.createPanel(state: TYConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return TYPanel(state, overrides, project).makeComponent()
}
