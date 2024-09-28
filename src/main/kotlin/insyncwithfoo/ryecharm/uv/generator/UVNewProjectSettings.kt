package insyncwithfoo.ryecharm.uv.generator

import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.message


internal enum class ProjectKind(override val label: String) : Labeled {
    APP(message("newProjectPanel.settings.projectKind.app")),
    LIBRARY(message("newProjectPanel.settings.projectKind.library")),
    PACKAGED_APP(message("newProjectPanel.settings.projectKind.packagedApp"));
}
