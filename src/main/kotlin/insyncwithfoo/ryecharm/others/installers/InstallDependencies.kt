package insyncwithfoo.ryecharm.others.installers

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv
import kotlinx.coroutines.CoroutineScope


private sealed class InstallKind {
    data class Group(val name: String) : InstallKind()
    data object AllGroups : InstallKind()
    data class Extra(val name: String) : InstallKind()
    data object AllExtras : InstallKind()
}


/**
 * Install a dependency group or extra using `uv sync`.
 * 
 * @see GutterInstallButtonsProvider
 */
internal class InstallDependencies private constructor(
    private val project: Project,
    private val kind: InstallKind
) : AnAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val uv = project.uv
        
        if (uv == null) {
            val debugNote = """
                |Was trying to install dependencies:
                |$kind
            """.trimMargin()
            
            return project.couldNotConstructCommandFactory<UV>(debugNote)
        }
        
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
        is InstallKind.Group -> uv.installGroup(kind.name)
        is InstallKind.AllGroups -> uv.installAllGroups()
        is InstallKind.Extra -> uv.installExtra(kind.name)
        is InstallKind.AllExtras -> uv.installAllExtras()
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
    companion object {
        
        fun group(project: Project, name: String) =
            InstallDependencies(project, InstallKind.Group(name))
        
        fun allGroups(project: Project) =
            InstallDependencies(project, InstallKind.AllGroups)
        
        fun extra(project: Project, name: String) =
            InstallDependencies(project, InstallKind.Extra(name))
        
        fun allExtras(project: Project) =
            InstallDependencies(project, InstallKind.AllExtras)
        
    }
    
}
