package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.LocalFileSystem
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.addCopyPathAction
import insyncwithfoo.ryecharm.error
import insyncwithfoo.ryecharm.errorNotificationGroup
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.openFile
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.toPathOrNull
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory


internal class OpenUserConfigurationFile : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val path = findUserConfigurationFile() ?: return project.fileNotFound()
        
        project.findAndOpenFile(path)
    }
    
    private fun Project.findAndOpenFile(path: Path) = launch<ActionCoroutine> {
        val virtualFile = runInBackground(message("progresses.command.rye.config")) {
            LocalFileSystem.getInstance().findFileByNioFile(path)
        }
        
        ProgressContext.UI.launch {
            openFile(virtualFile)
        }
    }
    
    /**
     * Find the `.ruff.toml`/`ruff.toml` file in `XDG_CONFIG_HOME` or:
     * 
     * * Windows: `~/AppData/Roaming/`
     * * Other: `~/.config/`
     * 
     * Ruff also supports user-level `pyproject.toml`
     * and `~/Library/Application Support/` (macOS),
     * both of which are deliberately not added here.
     */
    private fun findUserConfigurationFile(): Path? {
        val userHome = System.getProperty("user.home")?.toPathOrNull() ?: return null
        val xdgConfigHome = System.getenv("XDG_CONFIG_HOME")?.toPathOrNull()
        
        val configHome = when {
            xdgConfigHome != null -> xdgConfigHome
            SystemInfo.isWindows -> userHome / Path.of("AppData/Roaming")
            else -> userHome / Path.of(".config")
        }
        val parent = configHome / "ruff"
        
        if (!parent.isDirectory()) {
            return null
        }
        
        return listOf(".ruff.toml", "ruff.toml").firstNotNullOfOrNull { filename ->
            (parent / filename).normalize().takeIf { it.exists() }
        }
    }
    
    private fun Project.fileNotFound(path: Path? = null) {
        val title = message("notifications.fileNotFound.title")
        val body = when (path) {
            null -> message("notifications.fileNotFound.body")
            else -> message("notifications.fileNotFound.body.withPath", path)
        }
        
        errorNotificationGroup.error(title, body).runThenNotify(this) {
            if (path != null) {
                addCopyPathAction(path)
            }
        }
    }
    
}
