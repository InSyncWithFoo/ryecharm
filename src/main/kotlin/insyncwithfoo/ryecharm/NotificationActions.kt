package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.ui.EmptyClipboardOwner
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


internal fun Notification.addAction(text: String, action: () -> Unit) =
    addAction(NotificationAction.createSimple(text, action))


internal fun Notification.addExpiringAction(text: String, action: () -> Unit) =
    addAction(NotificationAction.createSimpleExpiring(text, action))


internal fun Notification.addOpenBrowserAction(text: String, link: String) =
    addAction(BrowseNotificationAction(text, link))


internal fun Notification.addOpenPluginIssueTrackerAction() {
    val text = message("notificationActions.openPluginIssueTracker")
    val link = RyeCharm.ISSUE_TRACKER
    
    addOpenBrowserAction(text, link)
}


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


private class OpenFileAction(text: String, private val path: String) : NotificationAction(text) {
    
    override fun actionPerformed(event: AnActionEvent, notification: Notification) {
        val project = event.project ?: return cannotOpenFile()
        
        val fileSystem = LocalFileSystem.getInstance()
        val virtualFile = fileSystem.findFileByPath(path) ?: return cannotOpenFile(project)
        
        project.fileEditorManager.openFileEditor(OpenFileDescriptor(project, virtualFile), true)
    }
    
    private fun cannotOpenFile(project: Project? = null) {
        val title = message("notifications.cannotOpenFile.title")
        val content = message("notifications.cannotOpenFile.body", path)
        
        project.somethingIsWrong(title, content)
    }
    
}


internal fun Notification.addOpenFileAction(text: String? = null, path: String) {
    addAction(OpenFileAction(text ?: message("notificationActions.openFile"), path))
}



internal class OpenTemporaryFileAction(
    text: String,
    private val filename: String,
    private val content: String
) : NotificationAction(text) {
    
    override fun actionPerformed(event: AnActionEvent, notification: Notification) {
        event.project!!.openLightFile(filename, content)
    }
    
}


internal fun Notification.addOpenTemporaryFileAction(text: String, filename: String, content: String) {
    addAction(OpenTemporaryFileAction(text, filename, content))
}


private fun Notification.addOpenTemporaryFileActionIfNotBlank(text: String, filename: String, content: String) {
    if (content.isNotBlank()) {
        addOpenTemporaryFileAction(text, filename, content)
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
