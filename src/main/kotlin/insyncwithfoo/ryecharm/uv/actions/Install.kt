package insyncwithfoo.ryecharm.uv.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.SystemInfo
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.RyeCharmRegistry
import insyncwithfoo.ryecharm.addExpiringAction
import insyncwithfoo.ryecharm.configurations.uv.UVConfigurable
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.confirm
import insyncwithfoo.ryecharm.create
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.importantNotificationGroup
import insyncwithfoo.ryecharm.information
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.showSettingsDialog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope


private suspend fun fetch(url: String) =
    HttpClient(CIO).use {
        it.get(url).body<String>()
    }


/**
 * Install uv semi-automatically if it isn't already installed.
 * 
 * If run as a [ProjectActivity],
 * emit a notification that recommends installing uv.
 */
internal class Install : AnAction(), ProjectActivity, DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val uvExecutable = project.uvExecutable
        
        if (uvExecutable != null) {
            val prompt = message("messages.uvIsAlreadyInstalled.body", uvExecutable)
            
            if (!project.confirm(prompt)) {
                return
            }
        }
        
        project.installUV()
    }
    
    override suspend fun execute(project: Project) {
        val uvExecutable = project.uvExecutable
        
        if (uvExecutable != null && !RyeCharmRegistry.uv.alwaysRunInstaller) {
            return
        }
        
        when (RyeCharmRegistry.uv.alwaysRunInstaller) {
            true -> project.recommendInstallingUV()
            else -> RunOnceUtil.runOnceForApp(ID) { project.recommendInstallingUV() }
        }
    }
    
    private fun Project.recommendInstallingUV() {
        val title = message("notifications.uvNotFound.title")
        val body = message("notifications.uvNotFound.body")
        
        importantNotificationGroup.information(title, body).runThenNotify(this) {
            addExpiringAction(message("notificationActions.install")) {
                installUV()
            }
            addExpiringAction(message("notificationActions.specifyExecutableManually")) {
                showSettingsDialog<UVConfigurable>()
            }
        }
    }
    
    private fun Project.installUV() = launch<Coroutine> {
        when (SystemInfo.isWindows) {
            true -> installOnWindows()
            else -> installOnOtherPlatforms()
        }
    }
    
    private suspend fun Project.installOnWindows() {
        val command = Command.create(
            "powershell", "-ExecutionPolicy", "ByPass", "-c", "irm https://astral.sh/uv/install.ps1 | iex",
            runningMessage = message("progresses.command.installUV")
        )
        
        runInBackground(command) { output ->
            notifyResult(command, output)
        }
    }
    
    private suspend fun Project.installOnOtherPlatforms() {
        val installScript = try {
            fetch("https://astral.sh/uv/install.sh")
        } catch (exception: Exception) {
            thisLogger().error(exception)
            return
        }
        val command = Command.create(
            "sh", "-c", installScript,
            runningMessage = message("progresses.command.installUV")
        )
        
        runInBackground(command) { output ->
            notifyResult(command, output)
        }
    }
    
    private fun Project.notifyResult(command: Command, output: ProcessOutput) {
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            val downloadNotice = """(?i)downloading uv (?<version>\S+)""".toRegex()
            val version = downloadNotice.find(output.streamWithDownloadNotice)?.groups["version"]?.value
            
            val message = when (version) {
                null -> message("notifications.installedUV.body")
                else -> message("notifications.installedUV.body.versioned", version)
            }
            
            processCompletedSuccessfully(message)
        }
    }
    
    private val ProcessOutput.streamWithDownloadNotice: String
        get() = when (SystemInfo.isWindows) {
            // https://github.com/axodotdev/cargo-dist/blob/96bef18d/cargo-dist/templates/installer/installer.ps1.j2#L224
            true -> stdout
            // https://github.com/axodotdev/cargo-dist/blob/96bef18d/cargo-dist/templates/installer/installer.sh.j2#L219
            else -> stderr
        }
    
    @Service(Service.Level.APP)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
    companion object {
        private const val ID = "${RyeCharm.ID}.uv.actions.Install"
    }
    
}
