package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.openProjects
import insyncwithfoo.ryecharm.restartNativeServers
import insyncwithfoo.ryecharm.toggleLSP4IJServers
import insyncwithfoo.ryecharm.ty.lsp.TYServerSupportProvider
import insyncwithfoo.ryecharm.ty.lsp4ij.SERVER_ID


private fun Project.toggleServers() {
    restartNativeServers<TYServerSupportProvider>()
    toggleLSP4IJServers(SERVER_ID, restart = tyConfigurations.runningMode == RunningMode.LSP4IJ)
}


internal class TYConfigurable : PanelBasedConfigurable<TYConfigurations>() {
    
    private val service = TYGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.ty.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        
        openProjects.forEach { project ->
            project.toggleServers()
        }
    }
    
}


internal class TYProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<TYConfigurations>(), ProjectBasedConfigurable {
    
    private val service = TYLocalService.getInstance(project)
    private val overrideService = TYOverrideService.getInstance(project)
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
