package insyncwithfoo.ryecharm.configurations.redknot

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.RyeCharmRegistry
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.redknot.commands.RedKnot
import insyncwithfoo.ryecharm.redknot.commands.detectExecutable
import insyncwithfoo.ryecharm.singleFileTextField


private class RedKnotPanel(state: RedKnotConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<RedKnotConfigurations>(state, overrides, project)


private fun Row.executableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.enableInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.redknot.enable.label")).apply(block)


private fun RedKnotPanel.makeComponent() = panel {
    
    row(message("configurations.redknot.executable.label")) {
        executableInput {
            val detectedExecutable = RedKnot.detectExecutable()?.toString()
            
            bindText(state::executable) { detectedExecutable.orEmpty() }
            emptyText = detectedExecutable ?: message("configurations.redknot.executable.placeholder")
        }
        overrideCheckbox(state::executable)
    }
    
    group {
        row {
            enableInput { bindSelected(state::enable) }
            overrideCheckbox(state::enable)
        }
    }
    
}


private fun makePlaceholderPanel() = panel {
    row {
        text(message("configurations.redknot.emptyPanel"), maxLineLength = 80)
    }
}


internal fun PanelBasedConfigurable<RedKnotConfigurations>.createPanel(state: RedKnotConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    
    return when (RyeCharmRegistry.redknot.panels) {
        true -> RedKnotPanel(state, overrides, project).makeComponent()
        else -> makePlaceholderPanel()
    }
}
