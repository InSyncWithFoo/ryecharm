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


private fun TYPanel.makeComponent() = panel {
    
    row(message("configurations.executable.label")) {
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
    
}


internal fun PanelBasedConfigurable<TYConfigurations>.createPanel(state: TYConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return TYPanel(state, overrides, project).makeComponent()
}
