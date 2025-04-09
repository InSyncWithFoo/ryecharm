package insyncwithfoo.ryecharm.uv.run.standalonescripts

import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationSettings


internal class UVStandaloneScriptSettings : UVRunConfigurationSettings() {
    var scriptPath by string()
    var scriptArguments by string()
    var extraArguments by string()
}
