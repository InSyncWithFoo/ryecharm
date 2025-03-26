package insyncwithfoo.ryecharm.uv.run.scripts

import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationSettings


internal class UVProjectScriptSettings : UVRunConfigurationSettings() {
    var script by string()
    var extraArguments by string()
}
