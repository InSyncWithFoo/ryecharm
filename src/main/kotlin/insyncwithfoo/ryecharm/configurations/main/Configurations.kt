package insyncwithfoo.ryecharm.configurations.main

import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName


internal class MainConfigurations : DisplayableState() {
    var languageInjectionPEP723Blocks by property(true)
    var languageInjectionRequirements by property(true)
    
    var suppressIncorrectNIRI by property(true)
    var suppressIncorrectNIRINonUVSDK by property(false)
    
    var consoleFilterRuffAndTYPaths by property(true)
}


internal class MainOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
