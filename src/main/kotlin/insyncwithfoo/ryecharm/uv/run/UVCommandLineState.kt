package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.util.execution.ParametersListUtil
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.processHandlerFactory
import insyncwithfoo.ryecharm.toPathIfItExists


internal abstract class UVCommandLineState<S : UVRunConfigurationSettings>(
    protected val settings: S,
    environment: ExecutionEnvironment
) : CommandLineState(environment) {
    
    protected val project by environment::project
    
    protected val executable: String
        get() = project.uvExecutable?.toString() ?: "uv"
    
    protected val commandLine: GeneralCommandLine
        get() = GeneralCommandLine(executable)
    
    protected fun GeneralCommandLine.buildProcessHandler(block: GeneralCommandLine.() -> Unit) = this.run {
        withWorkingDirectory(settings.workingDirectory?.toPathIfItExists() ?: project.path)
        withEnvironment(settings.environmentVariables)
        
        block()
        
        toProcessHandler()
    }
    
    private fun GeneralCommandLine.toProcessHandler(): ProcessHandler =
        processHandlerFactory.createColoredProcessHandler(this).also {
            (it as? ColoredProcessHandler)?.setShouldKillProcessSoftly(true)
            ProcessTerminatedListener.attach(it)
        }
    
    protected fun parseArguments(arguments: String): List<String> {
        val (keepQuotes, supportSingleQuotes, keepEmptyParameters) = Triple(false, true, true)
        
        return ParametersListUtil.parse(arguments, keepQuotes, supportSingleQuotes, keepEmptyParameters)
    }
    
}
