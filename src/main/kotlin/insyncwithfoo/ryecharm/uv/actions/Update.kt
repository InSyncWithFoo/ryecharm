package insyncwithfoo.ryecharm.uv.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.RyeCharmRegistry
import insyncwithfoo.ryecharm.addExpiringAction
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeGlobalUVConfigurations
import insyncwithfoo.ryecharm.configurations.changeUVConfigurations
import insyncwithfoo.ryecharm.configurations.changeUVOverrides
import insyncwithfoo.ryecharm.configurations.uv.UpdateMethod
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.information
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessful
import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.parseAsJSONLeniently
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.propertiesComponent
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.stringifyToJSON
import insyncwithfoo.ryecharm.unimportantNotificationGroup
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.selfVersion
import insyncwithfoo.ryecharm.uv.commands.uv
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.time.Duration.Companion.hours


private const val EXECUTABLES_TO_LAST_CHECKED = "${RyeCharm.ID}.uv.actions.Update.executablesToLastChecked"
private val CHECK_INTERVAL = 6.hours


private var executablesToLastChecked: Map<String, Instant>
    get() = propertiesComponent.getValue(EXECUTABLES_TO_LAST_CHECKED)?.parseAsJSON() ?: emptyMap()
    set(value) = propertiesComponent.setValue(EXECUTABLES_TO_LAST_CHECKED, value.stringifyToJSON())


@Serializable
private data class VersionInfo(val version: String)


/**
 * Update uv (semi-)automatically.
 */
internal class Update : AnAction(), ProjectActivity, DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val uv = project.uv
        
        if (uv == null) {
            project.couldNotConstructCommandFactory<UV>(
                """
                |Was trying to update uv.
                """.trimMargin()
            )
            return
        }
        
        updateCheckTime(uv)
        
        project.launch<Coroutine> {
            project.runUVSelfUpdate(uv)
        }
    }
    
    override suspend fun execute(project: Project) {
        val executable = project.uvExecutable ?: return
        val configurations = project.uvConfigurations
        
        when {
            configurations.updateMethod == UpdateMethod.DISABLED -> return
            !RyeCharmRegistry.uv.alwaysRunUpdater && alreadyRecommendedRecently(executable) -> return
        }
        
        project.checkVersionsAndUpdate()
    }
    
    private fun alreadyRecommendedRecently(executable: Path): Boolean {
        val executablesToLastChecked = executablesToLastChecked
        val timezone = TimeZone.currentSystemDefault()
        
        val now = Clock.System.now()
        val today = now.toLocalDateTime(timezone)
        val lastChecked = executablesToLastChecked[executable.toString()] ?: return false
        val lastCheckedDate = lastChecked.toLocalDateTime(timezone)
        
        return lastCheckedDate < today && now - lastChecked < CHECK_INTERVAL
    }
    
    private suspend fun Project.checkVersionsAndUpdate() {
        val uv = this.uv ?: return
        
        val currentVersion = getCurrentVersion(uv) ?: return
        val latestVersion = getLatestVersion(uv) ?: return
        
        updateCheckTime(uv)
        
        // TODO: What about custom builds?
        if (currentVersion == latestVersion) {
            return
        }
        
        when (uvConfigurations.updateMethod) {
            UpdateMethod.DISABLED -> {}
            UpdateMethod.AUTOMATIC -> runUVSelfUpdate(uv)
            UpdateMethod.NOTIFY -> recommendUpdating(uv, currentVersion, latestVersion)
        }
    }
    
    private suspend fun Project.getCurrentVersion(uv: UV): String? {
        val command = uv.selfVersion(json = true)
        val output = runInBackground(command)
        
        if (output.completedAbnormally) {
            notifyIfProcessIsUnsuccessful(command, output)
            return null
        }
        
        val versionInfo = output.stdout.parseAsJSONLeniently<VersionInfo>()
        
        return versionInfo?.version
    }
    
    private suspend fun Project.getLatestVersion(uv: UV): String? {
        val command = uv.pipCompile(packages = listOf("uv"))
        val output = runInBackground(command)
        
        if (output.completedAbnormally) {
            notifyIfProcessIsUnsuccessful(command, output)
            return null
        }
        
        val stdout = output.stdout
        val (_, version) = stdout.trim().split("==")
        
        return version
    }
    
    private fun updateCheckTime(uv: UV) {
        val executable = uv.executable.toString()
        
        executablesToLastChecked = executablesToLastChecked.toMutableMap().also {
            it[executable] = Clock.System.now()
        }
    }
    
    private fun Project.recommendUpdating(uv: UV, currentVersion: String, latestVersion: String) {
        val title = message("notifications.uvIsOutdated.title")
        val body = message("notifications.uvIsOutdated.body", currentVersion, latestVersion)
        val notification = unimportantNotificationGroup.information(title, body)
        
        notification.runThenNotify(this) {
            addExpiringAction(message("notificationActions.update")) {
                launch<Coroutine> { runUVSelfUpdate(uv) }
            }
            addExpiringAction(message("notificationActions.disableUpdater")) {
                changeGlobalUVConfigurations { updateMethod = UpdateMethod.DISABLED }
            }
            addExpiringAction(message("notificationActions.disableUpdaterForProject")) {
                changeUVConfigurations {
                    updateMethod = UpdateMethod.DISABLED
                    changeUVOverrides { add(::updateMethod.name) }
                }
            }
        }
    }
    
    private suspend fun Project.runUVSelfUpdate(uv: UV) {
        val command = uv.selfUpdate()
        val output = runInBackground(command)
        
        if (output.completedAbnormally) {
            return notifyIfProcessIsUnsuccessful(command, output)
        }
        
        val postUpdateVersion = """(?<=/tag/)\S+""".toRegex().find(output.stderr)?.value
        val content = postUpdateVersion?.let { message("notifications.updatedUV.body", it) }
        
        processCompletedSuccessfully(content)
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
