package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.util.execution.ParametersListUtil
import insyncwithfoo.ryecharm.toPathIfItExists
import insyncwithfoo.ryecharm.uv.run.UVCommandLineState


internal class UVCustomTaskCommandLineState(
    private val settings: UVCustomTaskSettings,
    environment: ExecutionEnvironment
) : UVCommandLineState(environment) {
    
    override fun startProcess(): ProcessHandler {
        val commandLine = GeneralCommandLine(executable).apply {
            withParameters(parseArguments())
            
            withWorkingDirectory(settings.workingDirectory?.toPathIfItExists())
            withEnvironment(settings.environmentVariables)
        }
        
        return commandLine.toProcessHandler()
    }
    
    private fun parseArguments(): List<String> {
        val (keepQuotes, supportSingleQuotes, keepEmptyParameters) = Triple(false, true, true)
        
        return ParametersListUtil.parse(
            settings.arguments.orEmpty(),
            keepQuotes,
            supportSingleQuotes,
            keepEmptyParameters
        )
    }
    
}
