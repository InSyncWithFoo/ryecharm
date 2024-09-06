package insyncwithfoo.ryecharm.uv.generator

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.jetbrains.python.newProject.PyNewProjectSettings
import com.jetbrains.python.newProject.steps.ProjectSpecificSettingsStep
import com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep
import com.jetbrains.python.sdk.PyLazySdk
import com.jetbrains.python.sdk.add.v2.PythonAddNewEnvironmentPanel
import insyncwithfoo.ryecharm.path
import javax.swing.JPanel


/**
 * @see PythonProjectSpecificSettingsStep
 */
internal class UVProjectSettingsStep(projectGenerator: UVProjectGenerator) :
    ProjectSpecificSettingsStep<UVNewProjectSettings>(projectGenerator, GenerateProjectCallback()), DumbAware {
    
    private val _settings = UVNewProjectSettings()
    val settings: UVNewProjectSettings
        get() = _settings.also {
            it.sdk = this.sdk
            it.baseInterpreter = panel.baseSDK?.path!!
        }
    
    private val panel by lazy { UVProjectSettingsStepPanel(_settings) }
    
    /**
     * Generate a name for the new project (and its own directory).
     */
    private val nextProjectDirectory by ::myProjectDirectory
    
    /**
     * Used by various functions in [ProjectSpecificSettingsStep].
     */
    private var projectLocationInput by ::myLocationField
    
    /**
     * @see PythonProjectSpecificSettingsStep.getProjectLocation
     */
    override fun getProjectLocation() = panel.projectLocation
    
    /**
     * Create the virtual environment and returns the SDK derived from that.
     *
     * @see com.jetbrains.python.sdk.add.v2.setupVirtualenv
     */
    override fun getSdk() = PyLazySdk("Uninitialized environment") {
        panel.venvCreator.createSdk()?.also { SdkConfigurationUtil.addSdk(it) }
            ?: error("Failed to create SDK")
    }
    
    /**
     * The original counterpart of this method,
     * [PythonProjectSpecificSettingsStep.getInterpreterInfoForStatistics],
     * is called by `PythonGenerateProjectCallback.computeProjectSettings`.
     * The return value is then given to [PyNewProjectSettings].
     *
     * `PythonGenerateProjectCallback`'s monkeypatch,
     * [GenerateProjectCallback], doesn't have a need for such information.
     * This method is only overridden here for documentation purposes.
     * 
     * @see PythonAddNewEnvironmentPanel.createStatisticsInfo
     */
    override fun getInterpreterInfoForStatistics() = null
    
    /**
     * @see getInterpreterInfoForStatistics
     */
    override fun installFramework() = false
    
    /**
     * @see getInterpreterInfoForStatistics
     */
    override fun createWelcomeScript() = false
    
    /**
     * @see getInterpreterInfoForStatistics
     */
    override fun getRemotePath() = null
    
    /**
     * Create the panel and set up listeners.
     * 
     * @see PythonProjectSpecificSettingsStep.createBasePanel
     */
    override fun createBasePanel(): JPanel {
        val panelComponent = recreateProjectCreationPanel()
        
        panel.setNewProjectName(nextProjectDirectory.get())
        panel.registerValidator { checkValid() }
        actionButton.addActionListener { panelComponent.apply() }
        
        projectLocationInput = panel.projectLocationInput
        
        return panelComponent
    }
    
    /**
     * Return the panel component created by
     * [UVProjectSettingsStepPanel.makeComponent].
     * 
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
    
}
