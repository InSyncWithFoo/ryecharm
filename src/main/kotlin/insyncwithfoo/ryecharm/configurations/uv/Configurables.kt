package insyncwithfoo.ryecharm.configurations.uv

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message


internal class UVConfigurable : PanelBasedConfigurable<UVConfigurations>() {
    
    private val service = UVGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.uv.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
    }
    
}


internal class UVProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<UVConfigurations>(), ProjectBasedConfigurable
{
    
    private val service = UVLocalService.getInstance(project)
    private val overrideService = UVOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
    }
    
}
