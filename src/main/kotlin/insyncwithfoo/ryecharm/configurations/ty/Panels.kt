package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindSelected
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.radioButtonForPotentiallyUnavailable
import insyncwithfoo.ryecharm.ty.commands.Ty
import insyncwithfoo.ryecharm.ty.commands.detectExecutable
import insyncwithfoo.ryecharm.singleFileTextField


private class TyPanel(state: TyConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<TyConfigurations>(state, overrides, project)


private fun Row.executableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Panel.runningModeInputGroup(block: Panel.() -> Unit) =
    buttonsGroup(init = block)


private fun TyPanel.makeComponent() = panel {
    
    row(message("configurations.ty.executable.label")) {
        executableInput {
            val detectedExecutable = Ty.detectExecutable()?.toString()
            
            bindText(state::executable) { detectedExecutable.orEmpty() }
            emptyText = detectedExecutable ?: message("configurations.ty.executable.placeholder")
        }
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
    
}


internal fun PanelBasedConfigurable<TyConfigurations>.createPanel(state: TyConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return TyPanel(state, overrides, project).makeComponent()
}
