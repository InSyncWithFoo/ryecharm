package insyncwithfoo.ryecharm.configurations.ruff

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.ruff.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class RuffGlobalService : ConfigurationService<RuffConfigurations>(RuffConfigurations()) {
    
    companion object {
        fun getInstance() = service<RuffGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.ruff.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class RuffLocalService : ConfigurationService<RuffConfigurations>(RuffConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RuffLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.ruff.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class RuffOverrideService : ConfigurationService<RuffOverrides>(RuffOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RuffOverrideService>()
    }
    
}


internal val globalRuffConfigurations: RuffConfigurations
    get() = RuffGlobalService.getInstance().state


internal val Project.ruffConfigurations: RuffConfigurations
    get() = getMergedState<RuffGlobalService, RuffLocalService, RuffOverrideService, _>()
