package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyProcessResult
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.clean
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import java.nio.file.Path


/**
 * Run `ruff clean`.
 */
internal class CleanCache : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val projectPath = project?.path
        val ruff = project?.ruff
        
        if (project == null || projectPath == null) {
            return noProjectFound()
        }
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to clean Ruff cache.
                """.trimMargin()
            )
            return
        }
        
        project.runCommandAndReport(ruff, projectPath)
    }
    
    private fun Project.runCommandAndReport(ruff: Ruff, path: Path) = launch<ActionCoroutine> {
        val command = ruff.clean(path)
        val output = runInBackground(command)
        
        notifyProcessResult(command, output)
    }
    
}
