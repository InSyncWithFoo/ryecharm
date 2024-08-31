package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runAction
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.showMessage
import insyncwithfoo.ryecharm.unableToRunCommand


internal class ShowVersion : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val ruff = project.ruff ?: return project.unableToRunCommand()
        
        project.runRuffVersionAndShowOutput(ruff)
    }
    
    private fun Project.runRuffVersionAndShowOutput(ruff: Ruff) = runAction {
        val command = ruff.version()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                showMessage(output.stdout)
            }
        }
    }
    
}
