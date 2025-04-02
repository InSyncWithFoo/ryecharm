package insyncwithfoo.ryecharm.uv.run.globaltools

import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationSettings


internal class UVXToolSettings : UVRunConfigurationSettings() {
    var toolName by string()
    var toolArguments by string()
    var fromPackage by string()
    var extraArguments by string()
}
