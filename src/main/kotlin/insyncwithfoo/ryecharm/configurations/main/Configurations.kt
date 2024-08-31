package insyncwithfoo.ryecharm.configurations.main

import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class MainConfigurations : DisplayableState(), Copyable {
    // FIXME: Remove this and its label as well as fix the tests
    var placeholderPropertyToAvoidImproperSerialization by property(false)
}


internal class MainOverrides : DisplayableState(), ProjectOverrideState {
    override var list by list<SettingName>()
}
