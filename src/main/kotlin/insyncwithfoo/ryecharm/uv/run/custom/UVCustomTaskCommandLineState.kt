package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVCustomTaskCommandLineState(settings: UVCustomTaskSettings, environment: ExecutionEnvironment) :
    UVCommandLineState<UVCustomTaskSettings>(settings, environment)
{
    
    override fun startProcess() =
        commandLine.buildProcessHandler {
            withParameters(parseArguments(settings.arguments.orEmpty()))
        }
    
}
