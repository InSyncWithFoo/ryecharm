package insyncwithfoo.ryecharm.configurations.rye

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.rye.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class RyeGlobalService : ConfigurationService<RyeConfigurations>(RyeConfigurations()) {
    
    companion object {
        fun getInstance() = service<RyeGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.rye.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class RyeLocalService : ConfigurationService<RyeConfigurations>(RyeConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RyeLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.rye.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class RyeOverrideService : ConfigurationService<RyeOverrides>(RyeOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RyeOverrideService>()
    }
    
}


internal val globalRyeConfigurations: RyeConfigurations
    get() = RyeGlobalService.getInstance().state


internal val Project.ryeConfigurations: RyeConfigurations
    get() = getMergedState<RyeGlobalService, RyeLocalService, RyeOverrideService, _>()
