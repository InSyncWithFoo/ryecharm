package insyncwithfoo.ryecharm.configurations.rye

import insyncwithfoo.ryecharm.Commented
import insyncwithfoo.ryecharm.Keyed
import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal class RyeConfigurations : DisplayableState(), HasTimeouts, Copyable {
    var executable by string(null)
    
    override var timeouts by map<SettingName, MillisecondsOrNoLimit>()
}


internal enum class RyeTimeouts(override val key: String, override val comment: String) : Keyed, Commented {
    SHOW("show", message("configurations.timeouts.rye.show")),
    VERSION("version", message("configurations.timeouts.rye.version")),
    BUILD("build", message("configurations.timeouts.rye.build")),
    PUBLISH("publish", message("configurations.timeouts.rye.publish")),
    MAKE_REQ("make-req", message("configurations.timeouts.rye.makereq")),
    CONFIG("config", message("configurations.timeouts.rye.config")),
    SELF_UPDATE("self update", message("configurations.timeouts.rye.selfupdate"));
    
    override val label by ::key
}


internal class RyeOverrides : DisplayableState(), ProjectOverrideState {
    override var list by list<SettingName>()
}
