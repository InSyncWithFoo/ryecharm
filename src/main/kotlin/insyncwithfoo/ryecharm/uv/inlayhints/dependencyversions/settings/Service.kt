package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import insyncwithfoo.ryecharm.configurations.ConfigurationService


@State(name = "insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions", storages = [Storage("ryecharm-others.xml")])
@Service(Service.Level.APP)
internal class SettingsService : ConfigurationService<Settings>(Settings()) {
    
    companion object {
        fun getInstance() = service<SettingsService>()
    }
    
}


internal val dependencyVersionInlayHintsSettings: Settings
    get() = SettingsService.getInstance().state
