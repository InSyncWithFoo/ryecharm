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


private sealed class InstallKind {
    data object All : InstallKind()
    class Group(val name: String) : InstallKind()
}


/**
 * Install a dependency group using `uv sync --group <group>`.
 * 
 * @see DependencyGroupInstaller
 */
internal class InstallDependencyGroup private constructor(
    private val project: Project,
    private val kind: InstallKind
) : AnAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val uv = project.uv ?: return project.unableToRunCommand()
        
        project.runUVSyncAndReport(uv)
    }
    
    private fun Project.runUVSyncAndReport(uv: UV) = launch<Coroutine> {
        val command = getCommand(uv)
        val output = runInBackground(command)
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            processCompletedSuccessfully(message("notifications.environmentSynchronized.body"))
        }
    }
    
    private fun getCommand(uv: UV) = when (kind) {
        is InstallKind.All -> uv.sync(allGroups = true)
        is InstallKind.Group -> uv.sync(group = kind.name)
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
    companion object {
        fun all(project: Project) =
            InstallDependencyGroup(project, InstallKind.All)
        
        fun group(project: Project, name: String) =
            InstallDependencyGroup(project, InstallKind.Group(name))
    }
    
}
