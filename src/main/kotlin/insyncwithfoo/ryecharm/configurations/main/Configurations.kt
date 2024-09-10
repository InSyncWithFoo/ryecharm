package insyncwithfoo.ryecharm.configurations.main

import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class MainConfigurations : DisplayableState(), Copyable {
    var pep723LanguageInjection by property(true)
}


internal class MainOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
