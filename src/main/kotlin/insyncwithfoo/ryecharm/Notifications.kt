package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project


internal typealias ErrorNotificationGroup = NotificationGroup
internal typealias InformationNotificationGroup = NotificationGroup


private const val ERROR_GROUP_ID = "insyncwithfoo.ryecharm.errors"
private const val INFORMATION_GROUP_ID = "insyncwithfoo.ryecharm.information"
private val ICON = RyeIcons.TINY_16


private val notificationGroupManager: NotificationGroupManager
    get() = NotificationGroupManager.getInstance()


internal val errorNotificationGroup: ErrorNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(ERROR_GROUP_ID)


internal val informationNotificationGroup: InformationNotificationGroup
    get() = notificationGroupManager.getNotificationGroup(INFORMATION_GROUP_ID)


private fun Notification.prettify() = this.apply {
    isImportant = false
    icon = ICON
}


internal fun Notification.runThenNotify(project: Project, action: Notification.() -> Unit) {
    run(action)
    notify(project)
}


internal fun ErrorNotificationGroup.error(title: String, content: String) =
    createNotification(title, content, NotificationType.ERROR).prettify()


internal fun ErrorNotificationGroup.warning(title: String, content: String) =
    createNotification(title, content, NotificationType.WARNING).prettify()


internal fun InformationNotificationGroup.information(title: String, content: String) =
    createNotification(title, content, NotificationType.INFORMATION).prettify()


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


internal fun Project.notifyProcessResult(command: Command, output: ProcessOutput) =
    notifyIfProcessIsUnsuccessfulOr(command, output) {
        processCompletedSuccessfully()
    }


internal fun ErrorNotificationGroup.genericWarning(content: String) =
    warning(message("notifications.warning.title"), content)


internal fun Project.notifyWarningsFromOutput(output: ProcessOutput) {
    val warning = """^warning: (.+)""".toRegex()
    val warnings = warning.findAll(output.stderr)
    
    warnings.forEach {
        val content = it.groups[1]!!.value
        
        errorNotificationGroup.genericWarning(content).runThenNotify(this) {
            addSeeOutputActions(output)
        }
    }
}


private fun InformationNotificationGroup.processCompletedSuccessfully(content: String? = null): Notification {
    val title = message("notifications.successful.title")
    val defaultContent = message("notifications.successful.body")
    
    return when (content) {
        null -> information("", defaultContent)
        else -> information(title, content)
    }
}


internal fun Project.processCompletedSuccessfully(content: String? = null) =
    informationNotificationGroup.processCompletedSuccessfully(content).notify(this)


private fun ErrorNotificationGroup.unknownError(
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


internal fun Project.unknownError(command: Command, processOutput: ProcessOutput? = null) =
    errorNotificationGroup.unknownError(command, processOutput).notify(this)


private fun ErrorNotificationGroup.processTimeout(command: Command): Notification {
    val title = message("notifications.processTimeout.title")
    val content = message("notifications.processTimeout.body", command)
    
    return warning(title, content)
}


internal fun Project.processTimeout(command: Command) =
    errorNotificationGroup.processTimeout(command).runThenNotify(this) {
        if (command is CommandWithTimeout) {
            addOpenSettingsAction(this@processTimeout, command.configurable)
        }
    }


private fun ErrorNotificationGroup.noProjectFound(): Notification {
    val title = message("notifications.noProjectFound.title")
    val content = message("notifications.noProjectFound.body")
    
    return error(title, content)
}


internal fun noProjectFound() =
    errorNotificationGroup.noProjectFound().notify(defaultProject)


private fun ErrorNotificationGroup.unableToRunCommand(): Notification {
    val title = message("notifications.unableToRunCommand.title")
    val content = message("notifications.unableToRunCommand.body")
    
    return error(title, content)
}


internal fun Project.unableToRunCommand() =
    errorNotificationGroup.unableToRunCommand().notify(this)


private fun ErrorNotificationGroup.noInterpreterFound(): Notification {
    val title = message("notifications.noInterpreterFound.title")
    val content = message("notifications.noInterpreterFound.body")
    
    return error(title, content)
}


internal fun Project.noInterpreterFound() =
    errorNotificationGroup.noInterpreterFound().notify(this)
