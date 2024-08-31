package insyncwithfoo.ryecharm.configurations.ruff

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServerManager
import com.redhat.devtools.lsp4ij.LanguageServerManager
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.isNormal
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.openProjects
import insyncwithfoo.ryecharm.ruff.linting.ruffInspectionisEnabled
import insyncwithfoo.ryecharm.ruff.lsp.RuffServerSupportProvider
import insyncwithfoo.ryecharm.ruff.lsp4ij.SERVER_ID


@Suppress("UnstableApiUsage")
private val Project.lspServerManager: LspServerManager
    get() = LspServerManager.getInstance(this)


@Suppress("UnstableApiUsage")
private fun Project.restartNativeServersIfSoChoose() {
    if (lspIsAvailable && this.isNormal && ruffConfigurations.autoRestartServer) {
        lspServerManager.stopAndRestartIfNeeded(RuffServerSupportProvider::class.java)
    }
}


private val Project.languageServerManager: LanguageServerManager
    get() = LanguageServerManager.getInstance(this)


private fun Project.stopLSP4IJServers(disable: Boolean = false) {
    val options = LanguageServerManager.StopOptions().apply {
        isWillDisable = disable
    }
    
    languageServerManager.stop(SERVER_ID, options)
}


private fun Project.startLSP4IJServers(enable: Boolean = false) {
    val options = LanguageServerManager.StartOptions().apply {
        isWillEnable = enable
    }
    
    languageServerManager.start(SERVER_ID, options)
}


private fun Project.toggleLSP4IJServersAccordingly() {
    if (!lsp4ijIsAvailable) {
        return
    }
    
    stopLSP4IJServers()
    
    if (ruffConfigurations.runningMode == RunningMode.LSP4IJ) {
        startLSP4IJServers()
    }
}


internal class RuffConfigurable : PanelBasedConfigurable<RuffConfigurations>() {
    
    private val service = RuffGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.ruff.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        
        openProjects.forEach { project ->
            // FIXME: Is toggling for all projects the correct thing to do?
            project.ruffInspectionisEnabled = state.runningMode == RunningMode.COMMAND_LINE
            
            project.restartNativeServersIfSoChoose()
            project.toggleLSP4IJServersAccordingly()
        }
    }
    
}


internal class RuffProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<RuffConfigurations>(), ProjectBasedConfigurable {
    
    private val service = RuffLocalService.getInstance(project)
    private val overrideService = RuffOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.list }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
        
        project.ruffInspectionisEnabled = state.runningMode == RunningMode.COMMAND_LINE
        
        project.restartNativeServersIfSoChoose()
        project.toggleLSP4IJServersAccordingly()
    }
    
}
