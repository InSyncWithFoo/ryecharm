package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.configurations.rye.ryeConfigurations
import insyncwithfoo.ryecharm.configurations.ryeExecutable
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.configurations.uvExecutable
import java.nio.file.Path


/**
 * If the process was cancelled, do nothing.
 * If the process timed out, call [processTimeout] and return.
 * If none of those cases happened, call [notifyWarningsFromOutput].
 * 
 * Then, if the process was successful (see [ProcessOutput.isSuccessful]),
 * call [handleSuccessfulOutput]. Otherwise, call [unknownError].
 */
internal fun Project.notifyIfProcessIsUnsuccessfulOr(
    command: Command,
    output: ProcessOutput,
    handleSuccessfulOutput: () -> Unit
) {
    if (output.isCancelled) {
        return
    }
    
    if (output.isTimeout) {
        return processTimeout(command)
    }
    
    notifyWarningsFromOutput(output)
    
    if (!output.isSuccessful) {
        return unknownError(command, output)
    }
    
    handleSuccessfulOutput()
}


internal fun Project.notifyIfProcessIsUnsuccessful(command: Command, output: ProcessOutput) =
    notifyIfProcessIsUnsuccessfulOr(command, output) {}


/**
 * Call [notifyIfProcessIsUnsuccessfulOr] with [processCompletedSuccessfully] as the callback.
 */
internal fun Project.notifyProcessResult(command: Command, output: ProcessOutput) =
    notifyIfProcessIsUnsuccessfulOr(command, output) {
        processCompletedSuccessfully()
    }


private fun NotificationGroup.genericWarning(content: String) =
    warning(message("notifications.warning.title"), content)


/**
 * Search for lines starting with `warning:` in stderr
 * and re-emit them as notifications.
 */
internal fun Project.notifyWarningsFromOutput(output: ProcessOutput) {
    val warning = """^warning: (.+)""".toRegex()
    val warnings = warning.findAll(output.stderr)
    
    warnings.forEach {
        val content = it.groups[1]!!.value
        
        importantNotificationGroup.genericWarning(content).runThenNotify(this) {
            addSeeOutputActions(output)
        }
    }
}


private fun NotificationGroup.processCompletedSuccessfully(content: String? = null): Notification {
    val title = message("notifications.successful.title")
    val defaultContent = message("notifications.successful.body")
    
    return when (content) {
        null -> information("", defaultContent)
        else -> information(title, content)
    }
}


/**
 * Emit a notification saying that the process has completed successfully
 * with [content] as the body.
 * 
 * Typically used when a process is neither cancelled nor timed out,
 * and the exit code is 0.
 */
internal fun Project.processCompletedSuccessfully(content: String? = null) =
    unimportantNotificationGroup.processCompletedSuccessfully(content).notify(this)


private fun NotificationGroup.unknownError(
    command: Command,
    processOutput: ProcessOutput? = null
): Notification {
    val title = message("notifications.unknownError.title")
    val body = message("notifications.unknownError.body", command.shortenedForm)
    
    return error(title, body).apply {
        processOutput?.let { addSeeOutputActions(it) }
        addCopyCommandAction(command)
        addOpenPluginIssueTrackerAction()
    }
}


/**
 * Emit a notification saying that an unknown error has happened.
 * 
 * Typically used when the process is neither cancelled nor timed out,
 * but the exit code is not 0.
 * 
 * To assist with debugging, the following actions are provided:
 * 
 * * See stdout/stderr (see [addSeeOutputActions])
 * * Copy command (see [addCopyCommandAction])
 * * Open plugin issue tracker (see [addOpenPluginIssueTrackerAction])
 */
internal fun Project.unknownError(command: Command, processOutput: ProcessOutput? = null) =
    importantNotificationGroup.unknownError(command, processOutput).notify(this)


private fun NotificationGroup.processTimeout(command: Command): Notification {
    val title = message("notifications.processTimeout.title")
    val body = message("notifications.processTimeout.body", command)
    
    return warning(title, body)
}


// FIXME: This might never be reached, as timeouts are no longer configurable.
internal fun Project.processTimeout(command: Command) =
    importantNotificationGroup.processTimeout(command).notify(this)


private fun NotificationGroup.noProjectFound(): Notification {
    val title = message("notifications.noProjectFound.title")
    val body = message("notifications.noProjectFound.body")
    
    return error(title, body)
}


/**
 * Emit a notification saying no project is found.
 * 
 * Typically used when a project-based action cannot be completed
 * due to the lack of a project context
 * (e.g., an event has no corresponding [Project],
 * the path of a [Project] cannot be determined).
 */
internal fun noProjectFound() =
    importantNotificationGroup.noProjectFound().notify(defaultProject)


private fun NotificationGroup.noDocumentFound(): Notification {
    val title = message("notifications.noDocumentFound.title")
    val body = message("notifications.noDocumentFound.body")
    
    return error(title, body)
}


/**
 * Emit a notification saying no [Document] is found.
 * 
 * Typically used when an editor-based action cannot be completed
 * due to the lack of an editor context
 * (e.g., user is not interacting with any editors).
 */
internal fun Project.noDocumentFound() =
    unimportantNotificationGroup.noDocumentFound().notify(this)


private fun NotificationGroup.unableToRunCommand(): Notification {
    val title = message("notifications.unableToRunCommand.title")
    val body = message("notifications.unableToRunCommand.body")
    
    return error(title, body)
}


/**
 * Emit a notification saying that the command in question
 * could not be run for some reason, as documented by [debugNote].
 * 
 * Typically used via [couldNotConstructCommandFactory].
 * 
 * The [debugNote] can be viewed using an action.
 * 
 * @see addOpenTemporaryFileAction
 */
internal fun Project.unableToRunCommand(debugNote: String) {
    val debugInfo = """
        |${debugNote}
        |
        |Project path: ${this.path}
        |Configurations:
        |    * Ruff: ${this.ruffConfigurations}
        |    * uv: ${this.uvConfigurations}
        |    * Rye: ${this.ryeConfigurations}
        |Resolved executables:
        |    * Ruff: ${this.ruffExecutable}
        |    * uv: ${this.uvExecutable}
        |    * Rye: ${this.ryeExecutable}
    """.trimMargin()
    
    return importantNotificationGroup.unableToRunCommand().runThenNotify(this) {
        val title = message("notificationActions.seeDebugInfo")
        
        addOpenTemporaryFileAction(title, "debug-info.txt", debugInfo)
    }
}


/**
 * Thin wrapper around [unableToRunCommand],
 * attaching this factory's class name as part of the debug note.
 * 
 * @see CommandFactory
 */
internal inline fun <reified F : CommandFactory> Project.couldNotConstructCommandFactory(extraNote: String) {
    unableToRunCommand(
        """
        |Could not construct command factory of type ${F::class.simpleName}.
        |${extraNote}
        """.trimMargin()
    )
}


private fun NotificationGroup.noInterpreterFound(): Notification {
    val title = message("notifications.noInterpreterFound.title")
    val body = message("notifications.noInterpreterFound.body")
    
    return error(title, body)
}


internal fun Project.noInterpreterFound() =
    importantNotificationGroup.noInterpreterFound().notify(this)


private fun NotificationGroup.cannotOpenFile(path: Path): Notification {
    val title = message("notifications.cannotOpenFile.title")
    val body = message("notifications.cannotOpenFile.body", path)
    
    return error(title, body).apply {
        addCopyPathAction(path)
    }
}


/**
 * Emit a notification saying the file at [path] cannot be opened.
 * 
 * Typically used when [openFile] has failed.
 */
internal fun Project.cannotOpenFile(path: Path) =
    importantNotificationGroup.cannotOpenFile(path).notify(this)
