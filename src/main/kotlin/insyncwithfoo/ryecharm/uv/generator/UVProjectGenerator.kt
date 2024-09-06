package insyncwithfoo.ryecharm.uv.generator

import com.intellij.facet.ui.ValidationResult
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
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
    
    /**
     * The displayed name of the panel.
     */
    override fun getName() = message("newProjectPanel.title")
    
    /**
     * This method never gets called.
     * The default implementation also returns `null`.
     */
    override fun getDescription() = null
    
    /**
     * The logo to be displayed beside the name of the panel.
     * 
     * Size: 18&times;18
     * 
     * It is supposed to be 16&times;16,
     * but with the paddings it would look too small.
     */
    override fun getLogo() = UVIcons.TINY_18
    
    /**
     * This method is supposed to be called by
     * [ProjectSettingsStepBase.checkValid].
     * However, [UVProjectSettingsStep.checkValid]
     * does not use its super implementation.
     * 
     * It is overridden here for documentation purposes only.
     */
    override fun validate(baseDirPath: String) = ValidationResult.OK!!
    
    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<UVNewProjectSettings>?,
        callback: AbstractCallback<UVNewProjectSettings>?
    ) =
        UVProjectSettingsStep(this)
    
}
