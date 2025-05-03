package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message


internal class RedKnotConfigurable : PanelBasedConfigurable<RedKnotConfigurations>() {
    
    private val service = RedKnotGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.redknot.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
    }
    
}


internal class RedKnotProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<RedKnotConfigurations>(), ProjectBasedConfigurable
{
    
    private val service = RedKnotLocalService.getInstance(project)
    private val overrideService = RedKnotOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
    }
    
}
