package insyncwithfoo.ryecharm.configurations.rye

import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class RyeConfigurations : DisplayableState() {
    var executable by string(null)
}


internal class RyeOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
