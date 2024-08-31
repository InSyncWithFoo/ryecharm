package insyncwithfoo.ryecharm.uv.generator

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.python.sdk.PySdkSettings
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.excludeInnerVirtualEnv
import com.jetbrains.python.sdk.getOrCreateAdditionalData
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.changeGlobalUVConfigurations
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.configurations.uv.globalUVConfigurations
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.somethingIsWrong
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.sdk.UVSDKAdditionalData
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.name


// FIXME: Rewrite this class.
internal class VenvCreator(private val uvExecutable: Path, private val projectPath: Path, private val baseSdk: Sdk) {
    
    /**
     * Example: "Python 3.12 &#91;uv&#93; (my-project)"
     */
    private val suggestedName: String
        get() {
            val pythonAndVersion = PythonSdkType.suggestBaseSdkName(baseSdk.homePath!!)
            return "$pythonAndVersion [uv] (${projectPath.name})"
        }
    
    /**
     * Expected to be set by [createVenv]
     * once the virtual environment is created.
     */
    private lateinit var venvDirectoryName: String
    
    private val venvRoot: Path
        get() = projectPath / venvDirectoryName
    
    /**
     * @see com.jetbrains.python.sdk.configuration.createVirtualEnvSynchronously
     */
    fun createSdk(): Sdk? {
        val newSdk = createAndSetUpSdk() ?: return null
        val venvRoot = venvRoot.toString().replace("\\", "/")
        val project: Project? = null
        
        newSdk.getOrCreateAdditionalData().associateWithModulePath(projectPath.toString())
        project.excludeInnerVirtualEnv(newSdk)
        PySdkSettings.instance.onVirtualEnvCreated(baseSdk, venvRoot, projectPath.toString())
        
        changeGlobalUVConfigurations { executable = uvExecutable.toString() }
        runWriteAction {
            newSdk.sdkModificator.commitChanges()
            newSdk.sdkAdditionalData?.markAsCommited()
        }
        
        return newSdk
    }
    
    /**
     * @see com.jetbrains.python.sdk.createSdkByGenerateTask
     */
    private fun createAndSetUpSdk(): Sdk? {
        createVenvSynchronously()
        
        return SdkConfigurationUtil.setupSdk(
            emptyList<Sdk>().toTypedArray(),
            findNewlyCreatedInterpreter(),
            PythonSdkType.getInstance(),
            false,
            UVSDKAdditionalData(),
            suggestedName
        )
    }
    
    /**
     * @see com.jetbrains.python.packaging.PyTargetEnvironmentPackageManager.createVirtualEnv
     */
    private fun findNewlyCreatedInterpreter(): VirtualFile {
        val interpreterPath = PythonSdkUtil.getPythonExecutable(venvRoot.toString())
        
        if (interpreterPath == null) {
            somethingIsWrong(message("messages.cannotCreateVenv.body"))
            error("Cannot create virtual environment at $venvRoot using $uvExecutable.")
        }
        
        return StandardFileSystems.local().refreshAndFindFileByPath(interpreterPath)!!
    }
    
    /**
     * @see com.jetbrains.python.packaging.PyTargetEnvironmentPackageManager.createVirtualEnv
     */
    @Suppress("DialogTitleCapitalization")
    private fun createVenvSynchronously() {
        val (canBeCanceled, project) = Pair(false, null)
        
        // TODO: Use runWithModalProgressBlocking instead.
        @Suppress("UsagesOfObsoleteApi")
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            { createVenv() },
            message("progresses.command.uv.venv"),
            canBeCanceled, project
        )
    }
    
    private fun createVenv() {
        val baseInterpreterPath = baseSdk.homePath!!.toPathOrNull()!!
        val uv = UV.create(uvExecutable, projectPath)
        
        val command = uv.venv(baseInterpreterPath)
        val timeout = globalUVConfigurations.timeouts[UVTimeouts.VENV.key] ?: HasTimeouts.NO_LIMIT
        
        val output = command.run(timeout)  // FIXME: Run this properly
        
        when (val newVenvName = extractNewVenvName(output)) {
            null -> somethingIsWrong(message("messages.uvReportedError.body"))
            else -> venvDirectoryName = newVenvName
        }
    }
    
    private fun extractNewVenvName(output: ProcessOutput): String? {
        val venvName = """(?<=Creating virtualenv at: ).+""".toRegex()
        
        if (output.isTimeout || output.isCancelled || !output.isSuccessful) {
            return null
        }
        
        return venvName.find(output.stderr)?.value
    }
    
}
