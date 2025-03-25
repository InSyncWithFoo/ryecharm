package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.processHandlerFactory


internal abstract class UVCommandLineState(environment: ExecutionEnvironment) : CommandLineState(environment) {
    
    protected val project by environment::project
    
    protected val executable: String
        get() = project.uvExecutable?.toString() ?: "uv"
    
    protected fun GeneralCommandLine.toProcessHandler(): ProcessHandler =
        processHandlerFactory.createColoredProcessHandler(this).also {
            (it as? ColoredProcessHandler)?.setShouldKillProcessSoftly(true)
            ProcessTerminatedListener.attach(it)
        }
    
}
