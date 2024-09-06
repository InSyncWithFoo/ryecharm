package insyncwithfoo.ryecharm.uv.generator

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.platform.ProjectGeneratorPeer
import com.jetbrains.python.newProject.steps.PythonProjectSpecificSettingsStep
import insyncwithfoo.ryecharm.moduleManager
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessful
import insyncwithfoo.ryecharm.rootManager
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.uv.commands.uv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Service(Service.Level.PROJECT)
private class InitializeProjectCoroutine(val scope: CoroutineScope)


private fun Project.runInitializer(action: suspend CoroutineScope.() -> Unit) {
    service<InitializeProjectCoroutine>().scope.launch(block = action)
}


private fun UVProjectGenerator.generateProject(
    settingsStep: UVProjectSettingsStep,
    settings: UVNewProjectSettings
) : Project? {
    val location = FileUtil.expandUserHome(settingsStep.projectLocation)
    return AbstractNewProjectStep.doGenerateProject(null, location, this, settings)
}


/**
 * Run `uv init` at the newly created project directory.
 */
private fun Project.initializeUsingUV(settings: UVNewProjectSettings) {
    val name = settings.distributionName
    val kind = settings.projectKind
    val createReadme = settings.createReadme
    val pinPython = settings.pinPython
    
    val command = uv!!.init(name, kind, createReadme, pinPython)
    
    runInitializer {
        val output = runInForeground(command)
        
        notifyIfProcessIsUnsuccessful(command, output)
    }
}


private fun Project.initializeGitRepository() {
    val module = moduleManager.modules.firstOrNull() ?: return
    val moduleRoot = module.rootManager.contentRoots.firstOrNull() ?: return
    
    PythonProjectSpecificSettingsStep.initializeGit(this, moduleRoot)
}


private fun Project.refreshTreeView() {
    val (async, recursive, reloadChildren) = Triple(true, true, true)
    
    guessProjectDir()?.let {
        VfsUtil.markDirtyAndRefresh(async, recursive, reloadChildren, it)
    }
}


/**
 * A reimplementation of `PythonGenerateProjectCallback`
 * (an `impl` class, and thus unlinkable in KDoc)
 * with many code paths removed or rewritten.
 * 
 * It is responsible for calling SDK-creating code
 * as well as initializing Git repository if necessary.
 */
internal class GenerateProjectCallback : AbstractCallback<UVNewProjectSettings>() {
    
    override fun consume(
        settingsStep: ProjectSettingsStepBase<UVNewProjectSettings>?,
        peer: ProjectGeneratorPeer<UVNewProjectSettings>
    ) {
        val generator = (settingsStep as UVProjectSettingsStep).projectGenerator as UVProjectGenerator
        
        val settings = settingsStep.settings
        val newProject = generator.generateProject(settingsStep, settings)
            ?: error("Failed to generate project")
        
        SdkConfigurationUtil.setDirectoryProjectSdk(newProject, settings.sdk!!)
        
        newProject.initializeUsingUV(settings)
        
        if (settings.initializeGit) {
            newProject.initializeGitRepository()
        }
        
        newProject.refreshTreeView()
    }
    
}
