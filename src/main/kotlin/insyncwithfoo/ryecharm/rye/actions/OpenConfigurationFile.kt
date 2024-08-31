package insyncwithfoo.ryecharm.rye.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.addCopyTextAction
import insyncwithfoo.ryecharm.error
import insyncwithfoo.ryecharm.fileEditorManager
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notificationGroup
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.runAction
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.rye
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.unknownError
import java.nio.file.Path


private fun NotificationGroup.cannotOpenFile(path: Path): Notification {
    val title = message("notifications.cannotOpenFile.title")
    val content = message("notifications.cannotOpenFile.body", path)
    
    return error(title, content)
}


internal class OpenConfigurationFile : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val rye = project.rye ?: return project.unableToRunCommand()
        
        project.runRyeConfigAndOpenFile(rye)
    }
    
    private fun Project.runRyeConfigAndOpenFile(rye: Rye) = runAction {
        val command = rye.config()
        val output = runInBackground(command)
        
        if (output.isCancelled) {
            return@runAction
        }
        
        if (output.isTimeout) {
            return@runAction processTimeout(command)
        }
        
        val path = output.stdout.trim().toPathOrNull()
        
        if (!output.isSuccessful || path == null) {
            return@runAction unknownError(command, output)
        }
        
        val virtualFile = runInBackground(message("progresses.command.rye.config")) {
            LocalFileSystem.getInstance().findFileByNioFile(path)
        }
        
        ProgressContext.UI.launch {
            openFile(path, virtualFile)
        }
    }
    
    private fun Project.openFile(path: Path, virtualFile: VirtualFile?) {
        val focusEditor = true
        
        try {
            fileEditorManager.openFile(virtualFile!!, focusEditor)
        } catch (_: Throwable) {
            notificationGroup.cannotOpenFile(path).runThenNotify(this) {
                addCopyTextAction(message("notificationActions.copyPathToClipboard"), path.toString())
            }
        }
    }
    
}
