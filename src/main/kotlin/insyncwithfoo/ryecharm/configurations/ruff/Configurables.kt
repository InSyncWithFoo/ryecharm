package insyncwithfoo.ryecharm.configurations.ruff

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.openProjects
import insyncwithfoo.ryecharm.restartNativeServers
import insyncwithfoo.ryecharm.ruff.lsp.RuffServerSupportProvider
import insyncwithfoo.ryecharm.ruff.lsp4ij.SERVER_ID
import insyncwithfoo.ryecharm.toggleLSP4IJServers


private fun Project.toggleServers() {
    if (ruffConfigurations.autoRestartServers) {
        restartNativeServers<RuffServerSupportProvider>()
        toggleLSP4IJServers(SERVER_ID, restart = ruffConfigurations.runningMode == RunningMode.LSP4IJ)
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
            project.toggleServers()
        }
    }
    
}


internal class RuffProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<RuffConfigurations>(), ProjectBasedConfigurable {
    
    private val service = RuffLocalService.getInstance(project)
    private val overrideService = RuffOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
        
        project.toggleServers()
    }
    
}
