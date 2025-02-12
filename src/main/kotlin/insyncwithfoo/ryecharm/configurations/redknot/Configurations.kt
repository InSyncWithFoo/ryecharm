package insyncwithfoo.ryecharm.configurations.redknot

import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class RedKnotConfigurations : DisplayableState(), Copyable {
    var executable by string(null)
}


internal class RedKnotOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
