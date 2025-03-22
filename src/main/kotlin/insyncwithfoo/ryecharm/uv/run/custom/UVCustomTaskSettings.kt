package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configurations.RunConfigurationOptions
import insyncwithfoo.ryecharm.configurations.Copyable


internal class UVCustomTaskSettings : RunConfigurationOptions(), Copyable {
    var arguments by string()
    var workingDirectory by string()
    var environmentVariables by map<String, String>()
}
