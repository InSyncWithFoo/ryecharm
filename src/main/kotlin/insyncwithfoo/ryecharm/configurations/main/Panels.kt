package insyncwithfoo.ryecharm.configurations.main

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.message


private class MainPanel(state: MainConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<MainConfigurations>(state, overrides, project)


private fun MainPanel.makeComponent() = panel {
    row {
        label(message("configurations.emptyPanel")).align(Align.CENTER)
    }
}


internal fun PanelBasedConfigurable<MainConfigurations>.createPanel(state: MainConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return MainPanel(state, overrides, project).makeComponent()
}

