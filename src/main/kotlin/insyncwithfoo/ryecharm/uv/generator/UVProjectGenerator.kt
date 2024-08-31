package insyncwithfoo.ryecharm.uv.generator

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.platform.DirectoryProjectGenerator
import com.jetbrains.python.newProject.PythonProjectGenerator
import insyncwithfoo.ryecharm.icons.UVIcons
import insyncwithfoo.ryecharm.message


/**
 * This class is responsible for returning the name and icon
 * of the "create new project" panel as well as serving as
 * the entrypoint to other classes.
 * 
 * It must remain a subclass of `PythonProjectGenerator` to be
 * listed in the same section as other Python project generators.
 */
internal class UVProjectGenerator :
    PythonProjectGenerator<UVNewProjectSettings>(), CustomStepProjectGenerator<UVNewProjectSettings> {
    
    override fun getName() = message("newProjectPanel.title")
    
    /**
     * This method never gets called.
     * The default implementation also returns `null`.
     */
    override fun getDescription() = null
    
    override fun getLogo() = UVIcons.TINY_18
    
    override fun getProjectSettings() = UVNewProjectSettings()
    
    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<UVNewProjectSettings>?,
        callback: AbstractCallback<UVNewProjectSettings>?
    ) =
        UVProjectSettingsStep(this)
    
}
