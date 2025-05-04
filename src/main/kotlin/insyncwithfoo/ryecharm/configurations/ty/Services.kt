package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.ty.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class TYGlobalService : ConfigurationService<TYConfigurations>(TYConfigurations()) {
    
    companion object {
        fun getInstance() = service<TYGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.ty.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class TYLocalService : ConfigurationService<TYConfigurations>(TYConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<TYLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.ty.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class TYOverrideService : ConfigurationService<TYOverrides>(TYOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<TYOverrideService>()
    }
    
}


internal val globalTYConfigurations: TYConfigurations
    get() = TYGlobalService.getInstance().state


internal val Project.tyConfigurations: TYConfigurations
    get() = getMergedState<TYGlobalService, TYLocalService, TYOverrideService, _>()
