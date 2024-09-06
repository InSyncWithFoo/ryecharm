package insyncwithfoo.ryecharm.uv.generator

import com.jetbrains.python.newProject.PyNewProjectSettings
import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.propertiesComponent


internal enum class ProjectKind(override val label: String) : Labeled {
    APP(message("newProjectPanel.settings.projectKind.app")),
    LIBRARY(message("newProjectPanel.settings.projectKind.library")),
    PACKAGED_APP(message("newProjectPanel.settings.projectKind.packagedApp"));
}


internal class UVNewProjectSettings : PyNewProjectSettings() {
    var initializeGit: Boolean
        get() = propertiesComponent.getBoolean("PyCharm.NewProject.Git")
        set(value) = propertiesComponent.setValue("PyCharm.NewProject.Git", value)
    
    var distributionName: String? = null
        set(value) {
            field = value?.takeIf { it.isNotEmpty() }
        }
    
    var projectKind = ProjectKind.APP
    var createReadme = true
    var pinPython = true
}
