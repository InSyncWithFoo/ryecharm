package insyncwithfoo.ryecharm.configurations.rye

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message


internal class RyeConfigurable : PanelBasedConfigurable<RyeConfigurations>() {
    
    private val service = RyeGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.rye.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
    }
    
}


internal class RyeProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<RyeConfigurations>(), ProjectBasedConfigurable {
    
    private val service = RyeLocalService.getInstance(project)
    private val overrideService = RyeOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
    }
    
}
