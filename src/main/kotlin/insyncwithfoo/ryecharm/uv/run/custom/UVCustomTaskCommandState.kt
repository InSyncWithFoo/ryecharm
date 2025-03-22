package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.util.execution.ParametersListUtil
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.toPathIfItExists


private val processHandlerFactory: ProcessHandlerFactory
    get() = ProcessHandlerFactory.getInstance()


internal class UVCustomTaskCommandState(
    private val settings: UVCustomTaskSettings,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {
    
    private val project by environment::project
    
    override fun startProcess(): ProcessHandler {
        val executable = project.uvExecutable?.toString() ?: "uv"
        
        val commandLine = GeneralCommandLine(executable).apply {
            withParameters(parseArguments())
            
            withWorkingDirectory(settings.workingDirectory?.toPathIfItExists())
            withEnvironment(settings.environmentVariables)
        }
        
        return processHandlerFactory.createColoredProcessHandler(commandLine).also {
            (it as? ColoredProcessHandler)?.setShouldKillProcessSoftly(true)
            ProcessTerminatedListener.attach(it)
        }
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
