package insyncwithfoo.ryecharm.uv.run.globaltools

import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVXToolCommandLineState(settings: UVXToolSettings, environment: ExecutionEnvironment) :
    UVCommandLineState<UVXToolSettings>(settings, environment)
{
    
    override fun startProcess() =
        commandLine.buildProcessHandler {
            withParameters("tool", "run")
            
            val fromPackage = settings.fromPackage
            
            if (!fromPackage.isNullOrBlank()) {
                withParameters("--from", fromPackage)
            }
            
            withParameters(parseArguments(settings.extraArguments.orEmpty()))
            
            withParameters(settings.toolName!!)
            withParameters(parseArguments(settings.toolArguments.orEmpty()))
        }
    
}
