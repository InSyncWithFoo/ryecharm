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
internal class TyGlobalService : ConfigurationService<TyConfigurations>(TyConfigurations()) {
    
    companion object {
        fun getInstance() = service<TyGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.ty.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class TyLocalService : ConfigurationService<TyConfigurations>(TyConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<TyLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.ty.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class TyOverrideService : ConfigurationService<TyOverrides>(TyOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<TyOverrideService>()
    }
    
}


internal val globalTyConfigurations: TyConfigurations
    get() = TyGlobalService.getInstance().state


internal val Project.tyConfigurations: TyConfigurations
    get() = getMergedState<TyGlobalService, TyLocalService, TyOverrideService, _>()
