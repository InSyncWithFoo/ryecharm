package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope


private typealias CommandRunner = suspend CoroutineScope.(Command) -> ProcessOutput
private typealias UICallback = suspend CoroutineScope.(ProcessOutput) -> Unit


internal suspend inline fun Project.runInBackground(command: Command) =
    runInBackground(command.runningMessage) _lambda@{
        command.run(this@runInBackground)
    }


internal suspend inline fun Project.runInForeground(command: Command) =
    runInForeground(command.runningMessage) _lambda@{
        command.run(this@runInForeground)
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


internal suspend inline fun Project.runInBackground(command: Command, noinline callback: UICallback) =
    runIOCommandThenUICallback(command, { runInBackground(it) }, callback)


internal suspend inline fun Project.runInForeground(command: Command, noinline callback: UICallback) =
    runIOCommandThenUICallback(command, { runInForeground(it) }, callback)
