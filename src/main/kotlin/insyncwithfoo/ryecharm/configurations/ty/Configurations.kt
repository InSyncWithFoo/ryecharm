package insyncwithfoo.ryecharm.configurations.ty

import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal enum class RunningMode(override val label: String) : Labeled {
    DISABLED(message("configurations.ty.runningMode.disabled")),
    LSP4IJ(message("configurations.ty.runningMode.lsp4ij")),
    LSP(message("configurations.ty.runningMode.lsp"));
}


internal class RedKnotConfigurations : DisplayableState(), Copyable {
    var executable by string(null)
    var runningMode by enum(RunningMode.DISABLED)
}


internal class RedKnotOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
