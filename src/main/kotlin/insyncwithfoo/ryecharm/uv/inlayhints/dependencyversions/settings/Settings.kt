package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings

import insyncwithfoo.ryecharm.configurations.DisplayableState


internal class Settings : DisplayableState() {
    var projectDependencies by property(true)
    var projectOptionalDependencies by property(true)
    
    var buildSystemRequires by property(false)
    
    var dependencyGroups by property(true)
    
    var uvConstraintDependencies by property(false)
    var uvDevDependencies by property(true)
    var uvOverrideDependencies by property(false)
    var uvUpgradePackage by property(false)
    
    var uvPipUpgradePackage by property(false)
}
