package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.message


private fun Row.tomlFieldInput(name: String, block: Cell<JBCheckBox>.() -> Unit) =
    checkBox("<html><code>$name</code></html>").apply(block)


internal fun createPanel(state: Settings) = panel {
    
    row {
        text(message("inlayHints.uv.dependencyVersions.settings.fields.label"))
    }
    indent {
        row {
            tomlFieldInput("project.dependencies") { bindSelected(state::projectDependencies) }
        }
        row {
            tomlFieldInput("project.optional-dependencies.*") { bindSelected(state::projectOptionalDependencies) }
        }
        row {
            tomlFieldInput("build-system.requires") { bindSelected(state::buildSystemRequires) }
        }
        row {
            tomlFieldInput("dependency-groups.*") { bindSelected(state::dependencyGroups) }
        }
        row {
            tomlFieldInput("uv.constraint-dependencies") { bindSelected(state::uvConstraintDependencies) }
        }
        row {
            tomlFieldInput("uv.dev-dependencies") { bindSelected(state::uvDevDependencies) }
        }
        row {
            tomlFieldInput("uv.override-dependencies") { bindSelected(state::uvOverrideDependencies) }
        }
        row {
            tomlFieldInput("uv.upgrade-package") { bindSelected(state::uvUpgradePackage) }
        }
        row {
            tomlFieldInput("uv.pip.upgrade-package") { bindSelected(state::uvPipUpgradePackage) }
        }
    }
    
}
