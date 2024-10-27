package insyncwithfoo.ryecharm.uv.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.showMessage
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv


internal class ShowVersion : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val uv = project.uv ?: return project.unableToRunCommand()
        
        project.runCommandAndShowOutput(uv)
    }
    
    private fun Project.runCommandAndShowOutput(uv: UV) = launch<ActionCoroutine> {
        val command = uv.version()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                showMessage(output.stdout)
            }
        }
    }
    
}
