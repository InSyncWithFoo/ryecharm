package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.message


private fun Row.inputForField(name: String, block: Cell<JBCheckBox>.() -> Unit) =
    checkBox("<html><code>$name</code></html>").apply(block)


internal fun createPanel(state: Settings) = panel {
    row {
        text(message("inlayHints.uv.dependencyVersions.settings.fields.label"))
    }
    indent {
        row {
            inputForField("project.dependencies") { bindSelected(state::projectDependencies) }
        }
        row {
            inputForField("project.optional-dependencies.*") { bindSelected(state::projectOptionalDependencies) }
        }
        row {
            inputForField("build-system.requires") { bindSelected(state::buildSystemRequires) }
        }
        row {
            inputForField("dependency-groups.*") { bindSelected(state::dependencyGroups) }
        }
        row {
            inputForField("uv.constraint-dependencies") { bindSelected(state::uvConstraintDependencies) }
        }
        row {
            inputForField("uv.dev-dependencies") { bindSelected(state::uvDevDependencies) }
        }
        row {
            inputForField("uv.override-dependencies") { bindSelected(state::uvOverrideDependencies) }
        }
        row {
            inputForField("uv.upgrade-package") { bindSelected(state::uvUpgradePackage) }
        }
        row {
            inputForField("uv.pip.upgrade-package") { bindSelected(state::uvPipUpgradePackage) }
        }
    }
}
