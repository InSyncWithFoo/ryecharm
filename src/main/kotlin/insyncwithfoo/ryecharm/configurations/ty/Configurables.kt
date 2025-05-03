package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message


internal class TyConfigurable : PanelBasedConfigurable<TyConfigurations>() {
    
    private val service = TyGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.ty.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
    }
    
}


internal class TyProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<TyConfigurations>(), ProjectBasedConfigurable
{
    
    private val service = TyLocalService.getInstance(project)
    private val overrideService = TyOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
    }
    
}
