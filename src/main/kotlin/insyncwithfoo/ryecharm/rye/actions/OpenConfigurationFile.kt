package insyncwithfoo.ryecharm.rye.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import insyncwithfoo.ryecharm.cannotOpenFile
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.openFile
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runUnderUIThread
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.rye
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.unknownError
import java.nio.file.Path


internal class OpenConfigurationFile : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val rye = project.rye
        
        if (rye == null) {
            project.couldNotConstructCommandFactory<Rye>(
                """
                |Was trying to open Rye configuration file.
                """.trimMargin()
            )
            return
        }
        
        project.runRyeConfigAndOpenFile(rye)
    }
    
    private fun Project.runRyeConfigAndOpenFile(rye: Rye) = launch<ActionCoroutine> {
        val command = rye.config()
        val output = runInBackground(command)
        
        if (output.isCancelled) {
            return@launch
        }
        
        if (output.isTimeout) {
            return@launch processTimeout(command)
        }
        
        val path = output.stdout.trim().toPathOrNull()
        
        if (!output.isSuccessful || path == null) {
            return@launch unknownError(command, output)
        }
        
        val virtualFile = runInBackground(message("progresses.command.rye.config")) {
            LocalFileSystem.getInstance().findFileByNioFile(path)
        }
        
        runUnderUIThread {
            tryOpeningFile(path, virtualFile)
        }
    }
    
    private fun Project.tryOpeningFile(path: Path, virtualFile: VirtualFile?) {
        try {
            openFile(virtualFile)
        } catch (_: Throwable) {
            cannotOpenFile(path)
        }
    }
    
}
