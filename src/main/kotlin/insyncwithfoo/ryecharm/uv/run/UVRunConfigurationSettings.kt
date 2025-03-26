package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.RunConfigurationOptions
import insyncwithfoo.ryecharm.configurations.Copyable


internal abstract class UVRunConfigurationSettings : RunConfigurationOptions(), Copyable {
    var workingDirectory by string()
    var environmentVariables by map<String, String>()
}
