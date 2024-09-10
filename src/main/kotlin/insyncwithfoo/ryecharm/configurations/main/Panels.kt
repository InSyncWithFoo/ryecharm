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


private fun Row.makePEP723LanguageInjectionInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.main.pep723LanguageInjection.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun MainPanel.makeComponent() = panel {
    group(message("configurations.main.groups.pep723")) {
        row {
            makePEP723LanguageInjectionInput { bindSelected(state::pep723LanguageInjection) }
            makeOverrideCheckboxIfApplicable(state::pep723LanguageInjection)
        }
    }
}


internal fun PanelBasedConfigurable<MainConfigurations>.createPanel(state: MainConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return MainPanel(state, overrides, project).makeComponent()
}
