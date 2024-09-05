package insyncwithfoo.ryecharm.uv.generator

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.jetbrains.python.newProject.steps.ProjectSpecificSettingsStep
import com.jetbrains.python.sdk.PyLazySdk
import javax.swing.JPanel


// TODO: Refactor this
/**
 * @see com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep
 */
internal class UVProjectSettingsStep(projectGenerator: UVProjectGenerator) :
    ProjectSpecificSettingsStep<UVNewProjectSettings>(projectGenerator, GenerateProjectCallback()), DumbAware {
    
    private val panel by lazy { UVProjectSettingsStepPanel(projectGenerator) }
    
    /**
     * Generates a name for the new project (and its own directory).
     */
    private val nextProjectDirectory by ::myProjectDirectory
    
    val initializeGit: Boolean
        get() = panel.initializeGit.get()
    
    var projectLocationInput by ::myLocationField
    
    /**
     * Whether the example `main.py` script should be created.
     * 
     * Always return `false`, as this task is delegated to `uv.
     */
    override fun createWelcomeScript() = false
    
    override fun getProjectLocation() = panel.projectLocation
    
    override fun getRemotePath() = null
    
    override fun createBasePanel(): JPanel {
        val panelComponent = recreateProjectCreationPanel()
        
        panel.setNewProjectName(nextProjectDirectory.get())
        panel.registerValidator { checkValid() }
        actionButton.addActionListener { panelComponent.apply() }
        
        projectLocationInput = panel.projectLocationInput
        
        return panelComponent
    }
    
    /**
     * Return the component created by [UVProjectSettingsStepPanel.makeComponent].
     * 
     * @see com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep.createBasePanel
     * @see com.jetbrains.python.sdk.add.v2.PythonAddNewEnvironmentPanel
     */
    private fun recreateProjectCreationPanel() = panel.makeComponent()
    
    /**
     * Called by [createBasePanel] and various other functions elsewhere.
     *
     * Responsible for setting the error notice
     * (at the bottom of the panel) if necessary.
     */
    override fun checkValid(): Boolean {
        val text = panel.getErrorText().also { setErrorText(it) }
        return text == null
    }
    
    /**
     * This method gets called when the panel
     * created by [createBasePanel] is selected.
     *
     * This deliberately does nothing.
     * The super implementation just calls some validation code
     * under a check which this class will never pass.
     */
    override fun onPanelSelected() {}
    
    /**
     * Create the virtual environment and returns the SDK derived from that.
     *
     * @see com.jetbrains.python.sdk.add.v2.setupVirtualenv
     */
    override fun getSdk() = PyLazySdk("Uninitialized environment") {
        panel.venvCreator.createSdk()?.also { SdkConfigurationUtil.addSdk(it) }
            ?: error("Failed to create SDK")
    }
    
}
