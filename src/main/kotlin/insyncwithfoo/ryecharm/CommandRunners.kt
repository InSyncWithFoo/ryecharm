package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope


private typealias CommandRunner = suspend CoroutineScope.(Command) -> ProcessOutput
private typealias UICallback = suspend CoroutineScope.(ProcessOutput) -> Unit


internal suspend fun Project.runInBackground(command: Command) =
    runInBackground(command.runningMessage) {
        command.run()
    }


internal suspend fun Project.runInForeground(command: Command) =
    runInForeground(command.runningMessage) {
        command.run()
    }


private suspend inline fun runIOCommandThenUICallback(
    command: Command,
    crossinline runCommand: CommandRunner,
    crossinline callback: UICallback
) {
    val output = runUnderIOThread {
        runCommand(command)
    }
    
    runUnderUIThread {
        callback(output)
    }
}


internal suspend fun Project.runInBackground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInBackground(it) }, callback)


internal suspend fun Project.runInForeground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInForeground(it) }, callback)
