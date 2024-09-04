package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import kotlinx.coroutines.CoroutineScope


internal typealias MillisecondsOrNoLimit = Int


internal interface CommandWithTimeout {
    
    val timeoutKey: String
    val configurable: Class<out PanelBasedConfigurable<*>>
    
    fun getTimeout(project: Project?): MillisecondsOrNoLimit?
    
}


private typealias CommandRunner = suspend CoroutineScope.(Command) -> ProcessOutput
private typealias UICallback = suspend CoroutineScope.(ProcessOutput) -> Unit


private fun Project.getCommandTimeout(command: Command) =
    (command as? CommandWithTimeout)?.getTimeout(this) ?: HasTimeouts.NO_LIMIT


internal suspend fun Project.runInBackground(command: Command) =
    runInBackground(command.runningMessage) {
        command.run(getCommandTimeout(command))
    }


internal suspend fun Project.runInForeground(command: Command) =
    runInForeground(command.runningMessage) {
        command.run(getCommandTimeout(command))
    }


private suspend inline fun runIOCommandThenUICallback(
    command: Command,
    crossinline runCommand: CommandRunner,
    crossinline callback: UICallback
) {
    val output = ProgressContext.IO.compute {
        runCommand(command)
    }
    
    ProgressContext.UI.launch {
        callback(output)
    }
}


internal suspend fun Project.runInBackground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInBackground(it) }, callback)


internal suspend fun Project.runInForeground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInForeground(it) }, callback)
