package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVStandaloneScriptCommandLineState(
    settings: UVStandaloneScriptSettings,
    environment: ExecutionEnvironment
) : UVCommandLineState<UVStandaloneScriptSettings>(settings, environment) {
    
    override fun startProcess() =
        commandLine.buildProcessHandler {
            withParameters("run")
            withParameters(parseArguments(settings.extraArguments.orEmpty()))
            
            withParameters("--script", settings.scriptPath!!)
            withParameters(parseArguments(settings.scriptArguments.orEmpty()))
        }
    
}
