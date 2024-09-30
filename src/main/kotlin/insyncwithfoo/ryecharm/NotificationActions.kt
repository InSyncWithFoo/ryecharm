package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.util.ui.EmptyClipboardOwner
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


internal fun Notification.addOpenPluginIssueTrackerAction(): Notification {
    val issueTrackerActionText = message("notificationActions.openPluginIssueTracker")
    return addAction(BrowseNotificationAction(issueTrackerActionText, RyeCharm.ISSUE_TRACKER))
}


internal fun Notification.addExpiringAction(text: String, action: () -> Unit) =
    addAction(NotificationAction.createSimpleExpiring(text, action))


internal fun Notification.addAction(text: String, action: () -> Unit) =
    addAction(NotificationAction.createSimple(text, action))


internal fun Notification.addCopyTextAction(text: String, content: String) {
    addAction(text) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(content), EmptyClipboardOwner.INSTANCE)
    }
}


internal fun Notification.addCopyTextAction(content: String) =
    addCopyTextAction(message("notificationActions.copyTextToClipboard"), content)


internal fun Notification.addCopyCommandAction(command: Command) =
    addCopyTextAction(message("notificationActions.copyCommandToClipboard"), command.toString())


private fun <C : PanelBasedConfigurable<*>> Project.showSettingsDialog(toSelect: Class<C>) {
    ShowSettingsUtil.getInstance().showSettingsDialog(this, toSelect)
}


internal fun Notification.addOpenSettingsAction(
    project: Project,
    configurableClass: Class<out PanelBasedConfigurable<*>>
) {
    addExpiringAction(message("notificationActions.openSettings")) {
        project.showSettingsDialog(configurableClass)
    }
}


internal class OpenTemporaryFileAction(
    text: String,
    private val fileName: String,
    private val content: String
) : NotificationAction(text) {
    
    override fun actionPerformed(event: AnActionEvent, notification: Notification) {
        event.project!!.openLightFile(fileName, content)
    }
    
}


internal fun Notification.addOpenTemporaryFileAction(text: String, fileName: String, content: String) {
    addAction(OpenTemporaryFileAction(text, fileName, content))
}


private fun Notification.addOpenTemporaryFileActionIfNotBlank(text: String, fileName: String, content: String) {
    if (content.isNotBlank()) {
        addOpenTemporaryFileAction(text, fileName, content)
    }
}


private fun Notification.addSeeStdoutAction(content: String) {
    val text = message("notificationActions.seeStdoutInEditor")
    addOpenTemporaryFileActionIfNotBlank(text, "stdout.txt", content)
}


private fun Notification.addSeeStderrAction(content: String) {
    val text = message("notificationActions.seeStderrInEditor")
    addOpenTemporaryFileActionIfNotBlank(text, "stderr.txt", content)
}


internal fun Notification.addSeeOutputActions(processOutput: ProcessOutput) {
    addSeeStdoutAction(processOutput.stdout)
    addSeeStderrAction(processOutput.stderr)
}
