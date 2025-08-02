package insyncwithfoo.ryecharm.ty.actions

import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.RyeCharmRegistry
import insyncwithfoo.ryecharm.addExpiringAction
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeGlobalTYConfigurations
import insyncwithfoo.ryecharm.configurations.changeTYConfigurations
import insyncwithfoo.ryecharm.configurations.changeTYOverrides
import insyncwithfoo.ryecharm.configurations.ty.RunningMode
import insyncwithfoo.ryecharm.configurations.ty.TYConfigurable
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.importantNotificationGroup
import insyncwithfoo.ryecharm.information
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.showSettingsDialog


/**
 * Enable ty if it is installed.
 */
internal class Enable : AnAction(), ProjectActivity, DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        
        project.showSettingsDialog<TYConfigurable>()
    }
    
    override suspend fun execute(project: Project) {
        val configurations = project.tyConfigurations
        val executable = project.tyExecutable
        
        if (executable == null || configurations.runningMode != RunningMode.DISABLED) {
            return
        }
        
        val preferredRunningMode = when {
            lspIsAvailable -> RunningMode.LSP
            lsp4ijIsAvailable -> RunningMode.LSP4IJ
            else -> return
        }
        
        when (RyeCharmRegistry.ty.alwaysRunEnabler) {
            true -> project.recommendEnablingTY(preferredRunningMode)
            else -> RunOnceUtil.runOnceForApp(ID) { project.recommendEnablingTY(preferredRunningMode) }
        }
    }
    
    private fun Project.recommendEnablingTY(mode: RunningMode) {
        val title = message("notifications.tyNotEnabled.title")
        val body = message("notifications.tyNotEnabled.body")
        
        importantNotificationGroup.information(title, body).runThenNotify(this) {
            addExpiringAction(message("notificationActions.enable")) {
                changeGlobalTYConfigurations { runningMode = mode }
            }
            addExpiringAction(message("notificationActions.enableForThisProject")) {
                changeTYConfigurations {
                    runningMode = mode
                    changeTYOverrides { add(::runningMode.name) }
                }
            }
        }
    }
    
    companion object {
        private const val ID = "${RyeCharm.ID}.ty.actions.Enable"
    }
    
}
