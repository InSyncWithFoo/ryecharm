package insyncwithfoo.ryecharm.others.dependencygroups

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv
import kotlinx.coroutines.CoroutineScope


internal class InstallDependencyGroup(private val project: Project, private val group: String) : AnAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val uv = project.uv ?: return project.unableToRunCommand()
        
        project.runUVSyncAndReport(uv)
    }
    
    private fun Project.runUVSyncAndReport(uv: UV) = launch<Coroutine> {
        val command = uv.sync(group)
        val output = runInBackground(command)
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            processCompletedSuccessfully(message("notifications.environmentSynchronized.body"))
        }
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
