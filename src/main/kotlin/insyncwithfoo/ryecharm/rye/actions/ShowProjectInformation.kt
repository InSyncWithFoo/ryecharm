package insyncwithfoo.ryecharm.rye.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.openLightFile
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.rye
import insyncwithfoo.ryecharm.unableToRunCommand


internal class ShowProjectInformation : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val rye = project.rye ?: return project.unableToRunCommand()
        
        project.runCommandAndShowOutput(rye)
    }
    
    private fun Project.runCommandAndShowOutput(rye: Rye) = runAction {
        val command = rye.show()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                openLightFile("stdout.txt", output.stdout)
            }
        }
    }
    
}
