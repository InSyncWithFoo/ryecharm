package insyncwithfoo.ryecharm.configurations.uv

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.uv.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class UVGlobalService : ConfigurationService<UVConfigurations>(UVConfigurations()) {
    
    companion object {
        fun getInstance() = service<UVGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.uv.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class UVLocalService : ConfigurationService<UVConfigurations>(UVConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<UVLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.uv.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class UVOverrideService : ConfigurationService<UVOverrides>(UVOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<UVOverrideService>()
    }
    
}


internal val globalUVConfigurations: UVConfigurations
    get() = UVGlobalService.getInstance().state


internal val Project.uvConfigurations: UVConfigurations
    get() = getMergedState<UVGlobalService, UVLocalService, UVOverrideService, _>()
