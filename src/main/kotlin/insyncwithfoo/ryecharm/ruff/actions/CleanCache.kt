package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyProcessResult
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runAction
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unableToRunCommand
import java.nio.file.Path


internal class CleanCache : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val projectPath = project?.path
        val ruff = project?.ruff
        
        if (project == null || projectPath == null) {
            return noProjectFound()
        }
        
        if (ruff == null) {
            return project.unableToRunCommand()
        }
        
        project.runCommandAndReport(ruff, projectPath)
    }
    
    private fun Project.runCommandAndReport(ruff: Ruff, path: Path) = runAction {
        val command = ruff.clean(path)
        val output = runInBackground(command)
        
        notifyProcessResult(command, output)
    }
    
}
