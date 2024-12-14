package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.showMessage


internal class ShowVersion : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val ruff = project.ruff
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to retrieve Ruff version.
                """.trimMargin()
            )
            return
        }
        
        project.runRuffVersionAndShowOutput(ruff)
    }
    
    private fun Project.runRuffVersionAndShowOutput(ruff: Ruff) = launch<ActionCoroutine> {
        val command = ruff.version()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                showMessage(output.stdout)
            }
        }
    }
    
}
