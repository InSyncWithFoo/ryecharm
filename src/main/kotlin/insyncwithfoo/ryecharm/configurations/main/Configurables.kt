package insyncwithfoo.ryecharm.configurations.main

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.ProjectBasedConfigurable
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message


internal class MainConfigurable : PanelBasedConfigurable<MainConfigurations>() {
    
    private val service = MainGlobalService.getInstance()
    
    override val state = service.state.copy()
    override val panel by lazy { createPanel(state) }
    
    override fun getDisplayName() = message("configurations.main.displayName")
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
    }
    
}


internal class MainProjectConfigurable(override val project: Project) :
    PanelBasedConfigurable<MainConfigurations>(), ProjectBasedConfigurable {
    
    private val service = MainLocalService.getInstance(project)
    private val overrideService = MainOverrideService.getInstance(project)
    private val overrideState = overrideService.state.copy()
    
    override val state = service.state.copy()
    override val overrides by lazy { overrideState.names }
    override val panel by lazy { createPanel(state) }
    
    override fun afterApply() {
        syncStateWithService(state, service.state)
        syncStateWithService(overrideState, overrideService.state)
    }
    
}
