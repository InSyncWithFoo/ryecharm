package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class UVConfigurations : DisplayableState() {
    var executable by string(null)
    var configurationFile by string(null)
    
    var packageManaging by property(true)
    var packageManagingNonUVProject by property(false)
    
    var retrieveDependenciesInReadAction by property(false)
    var dependenciesDataMaxAge by property(5)
}


internal class UVOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
