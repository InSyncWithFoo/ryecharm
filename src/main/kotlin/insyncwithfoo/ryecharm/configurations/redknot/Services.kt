package insyncwithfoo.ryecharm.configurations.redknot

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.redknot.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class RedKnotGlobalService : ConfigurationService<RedKnotConfigurations>(RedKnotConfigurations()) {
    
    companion object {
        fun getInstance() = service<RedKnotGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.redknot.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class RedKnotLocalService : ConfigurationService<RedKnotConfigurations>(RedKnotConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RedKnotLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.redknot.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class RedKnotOverrideService : ConfigurationService<RedKnotOverrides>(RedKnotOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<RedKnotOverrideService>()
    }
    
}


internal val globalRedKnotConfigurations: RedKnotConfigurations
    get() = RedKnotGlobalService.getInstance().state


internal val Project.redKnotConfigurations: RedKnotConfigurations
    get() = getMergedState<RedKnotGlobalService, RedKnotLocalService, RedKnotOverrideService, _>()
