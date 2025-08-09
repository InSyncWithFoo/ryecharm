package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal enum class UpdateMethod(override val label: String) : Labeled {
    DISABLED(message("configurations.uv.updateMethod.disabled")),
    NOTIFY(message("configurations.uv.updateMethod.notify")),
    AUTOMATIC(message("configurations.uv.updateMethod.automatic"));
}


internal class UVConfigurations : DisplayableState() {
    var executable by string(null)
    var configurationFile by string(null)
    
    var showDependencyTreesOnHover by property(true)
    var showVersionSpecifiersForDependencies by property(true)
    var showLatestVersionsForDependencies by property(false)
    var dedupeDependencyTrees by property(true)
    var dependencyTreeDepth by property(255)
    var showInvertedDependencyTreeFirst by property(false)
    
    var updateMethod by enum<UpdateMethod>(UpdateMethod.NOTIFY)
    
    var retrieveDependenciesInReadAction by property(false)
    var dependenciesDataMaxAge by property(5)
    var suppressIncorrectNIRI by property(true)
    var suppressIncorrectNIRINonUVSDK by property(false)
}


internal class UVOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
