package insyncwithfoo.ryecharm.uv.run.projectscripts

import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVProjectScriptCommandLineState(settings: UVProjectScriptSettings, environment: ExecutionEnvironment) :
    UVCommandLineState<UVProjectScriptSettings>(settings, environment) {
    
    override fun startProcess() =
        commandLine.buildProcessHandler {
            withParameters("run", settings.scriptName!!)
            withParameters(parseArguments(settings.extraArguments.orEmpty()))
        }
    
}
