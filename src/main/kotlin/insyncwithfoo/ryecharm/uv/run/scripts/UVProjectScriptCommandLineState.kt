package insyncwithfoo.ryecharm.uv.run.scripts

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.toPathIfItExists
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVProjectScriptCommandLineState(
    private val settings: UVProjectScriptSettings,
    environment: ExecutionEnvironment
) : UVCommandLineState(environment) {
    
    override fun startProcess(): ProcessHandler {
        val commandLine = GeneralCommandLine(executable).apply {
            withParameters("run", settings.script!!)
            withParameters(parseArguments(settings.extraArguments.orEmpty()))
            
            withWorkingDirectory(settings.workingDirectory?.toPathIfItExists())
            withEnvironment(settings.environmentVariables)
        }
        
        return commandLine.toProcessHandler()
    }
    
}
