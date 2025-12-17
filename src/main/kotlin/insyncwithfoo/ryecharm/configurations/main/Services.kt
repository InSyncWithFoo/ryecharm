package insyncwithfoo.ryecharm.configurations.main

import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ConfigurationService
import insyncwithfoo.ryecharm.configurations.getMergedState


@State(name = "insyncwithfoo.ryecharm.configurations.main.Global", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.APP)
internal class MainGlobalService : ConfigurationService<MainConfigurations>(MainConfigurations()) {
    
    companion object {
        fun getInstance() = service<MainGlobalService>()
    }
    
}


@State(name = "insyncwithfoo.ryecharm.configurations.main.Local", storages = [Storage("ryecharm.xml")])
@Service(Service.Level.PROJECT)
internal class MainLocalService : ConfigurationService<MainConfigurations>(MainConfigurations()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<MainLocalService>()
    }
    
}


@State(
    name = "insyncwithfoo.ryecharm.configurations.main.Override",
    storages = [Storage("ryecharm-overrides.xml", roamingType = RoamingType.DISABLED)]
)
@Service(Service.Level.PROJECT)
internal class MainOverrideService : ConfigurationService<MainOverrides>(MainOverrides()) {
    
    companion object {
        fun getInstance(project: Project) = project.service<MainOverrideService>()
    }
    
}


internal val globalMainConfigurations: MainConfigurations
    get() = MainGlobalService.getInstance().state


internal val Project.mainConfigurations: MainConfigurations
    get() = getMergedState<MainGlobalService, MainLocalService, MainOverrideService, _, _>()
