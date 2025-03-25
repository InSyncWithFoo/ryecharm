package insyncwithfoo.ryecharm.uv.run.custom

import insyncwithfoo.ryecharm.uv.run.CopyableRunConfigurationSettings


internal class UVCustomTaskSettings : CopyableRunConfigurationSettings() {
    var arguments by string()
    var workingDirectory by string()
    var environmentVariables by map<String, String>()
}
