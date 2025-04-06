package insyncwithfoo.ryecharm.uv.run.projectscripts

import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationSettings


internal class UVProjectScriptSettings : UVRunConfigurationSettings() {
    var scriptName by string()
    var scriptArguments by string()
    var extraArguments by string()
}
