package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.startup.ProjectActivity
import com.jetbrains.python.packaging.common.PythonPackageManagementListener
import insyncwithfoo.ryecharm.InformationNotificationGroup
import insyncwithfoo.ryecharm.addExpiringAction
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.errorNotificationGroup
import insyncwithfoo.ryecharm.findExecutableInVenv
import insyncwithfoo.ryecharm.information
import insyncwithfoo.ryecharm.interpreterPath
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.runThenNotify
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension


private fun InformationNotificationGroup.suggestExecutable(executableRelativized: Path) =
    information(
        title = message("notifications.suggestExecutable.title"),
        content = message("notifications.suggestExecutable.body", executableRelativized)
    )


private fun InformationNotificationGroup.noExecutableFound() =
    information(
        title = message("notifications.noExecutableFound.title"),
        content = message("notifications.noExecutableFound.content")
    )


private fun Project.noExecutableFound() =
    errorNotificationGroup.noExecutableFound().notify(this)


private fun Project.setAsExecutable(newValue: String, crossPlatform: Boolean) {
    changeRuffConfigurations {
        executable = newValue
        crossPlatformExecutableResolution = crossPlatform
        
        changeRuffOverrides {
            add(::executable.name)
            add(::crossPlatformExecutableResolution.name)
        }
    }
}


private fun Project.disableSuggester() {
    changeRuffConfigurations {
        suggestExecutableOnProjectOpen = false
        suggestExecutableOnPackagesChange = false
        
        changeRuffOverrides {
            add(::suggestExecutableOnProjectOpen.name)
            add(::suggestExecutableOnPackagesChange.name)
        }
    }
}


internal fun Project.suggestExecutable(executable: Path) {
    val projectPath = path ?: return
    val executableRelativized = projectPath.relativize(executable)
    
    val notification = errorNotificationGroup.suggestExecutable(executableRelativized)
    
    notification.runThenNotify(this) {
        addExpiringAction(message("notificationActions.setNameOnly")) {
            setAsExecutable(executable.nameWithoutExtension, crossPlatform = true)
        }
        addExpiringAction(message("notificationActions.setAbsolutePath")) {
            setAsExecutable(executable.toString(), crossPlatform = false)
        }
        addExpiringAction(message("notificationActions.disableSuggester")) {
            disableSuggester()
        }
    }
}


private val Project.interpreterIsLocal: Boolean
    get() = when {
        path == null -> false
        interpreterPath == null -> false
        else -> interpreterPath!!.startsWith(path!!)
    }


private fun Project.findAndSuggestExecutableOr(callback: () -> Unit) {
    when (val potentialExecutable = findExecutableInVenv("ruff")) {
        null -> callback()
        else -> suggestExecutable(potentialExecutable)
    }
}


/**
 * Suggest potential Ruff executables on:
 * 
 * * Action run ([AnAction]/[actionPerformed])
 * * Project open ([ProjectActivity]/[execute])
 * * Packages change ([PythonPackageManagementListener]/[packagesChanged])
 */
@Suppress("UnstableApiUsage")
internal class SuggestProjectExecutable(private val project: Project? = null) :
    AnAction(), PythonPackageManagementListener, ProjectActivity, DumbAware {
    
    private val doNothing = {}
    
    override fun packagesChanged(sdk: Sdk) = project!!.run {
        val configurations = ruffConfigurations
        val suggestionIsAllowed = configurations.suggestExecutableOnPackagesChange
        val noProjectExecutableGiven = configurations.executable == null
        
        if (suggestionIsAllowed && noProjectExecutableGiven && interpreterIsLocal) {
            project.findAndSuggestExecutableOr(doNothing)
        }
    }
    
    override suspend fun execute(project: Project) = project.run {
        val configurations = ruffConfigurations
        val suggestionIsAllowed = configurations.suggestExecutableOnProjectOpen
        val noProjectExecutableGiven = configurations.executable == null
        
        if (suggestionIsAllowed && noProjectExecutableGiven && interpreterIsLocal) {
            project.findAndSuggestExecutableOr(doNothing)
        }
    }
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        
        if (project == null || project.isDefault) {
            return noProjectFound()
        }
        
        project.runAction {
            project.findAndSuggestExecutableOr {
                project.noExecutableFound()
            }
        }
    }
    
}
