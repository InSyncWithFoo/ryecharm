package insyncwithfoo.ryecharm.uv.managing

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VfsUtil
import com.jetbrains.python.packaging.bridge.PythonPackageManagementServiceBridge
import com.jetbrains.python.packaging.common.PythonPackage
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.pip.PipRepositoryManager
import com.jetbrains.python.sdk.PythonSdkUpdater
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.uv.commands.UV
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


private val ProcessOutput.completedAbnormally: Boolean
    get() = isTimeout || isCancelled || !isSuccessful


private fun PythonPackage(surrogate: PythonPackageSurrogate) =
    with(surrogate) { PythonPackage(name, version) }


@Serializable
private class PythonPackageSurrogate(
    val name: String,
    val version: String,
    @SerialName("editable_project_location")
    val editableProjectLocation: String? = null
)


/**
 * Handle install/uninstall/reload operations
 * triggered via the *Python Packages* toolwindow.
 * 
 * According to [PythonPackageManagementServiceBridge.installPackage],
 * the errors wrapped in [Result]s must be [ExecutionException]s.
 * 
 * @see UVReportedError
 */
@Suppress("UnstableApiUsage")
internal class UVPackageManager(private val uv: UV, project: Project, sdk: Sdk) : PythonPackageManager(project, sdk) {
    
    override var installedPackages: List<PythonPackage> = emptyList()
        private set
    
    override val repositoryManager by lazy { PipRepositoryManager(project, sdk) }
    
    override suspend fun reloadPackages(): Result<List<PythonPackage>> {
        val command = uv.pipList()
        val output = project.runInBackground(command)
        val packages = parsePipListOutput(output.stdout)
        
        if (output.completedAbnormally || packages == null) {
            return Result.failure(UVReportedError(output))
        }
        
        return Result.success(packages).also { installedPackages = packages }
    }
    
    private fun parsePipListOutput(raw: String): List<PythonPackage>? {
        val json = Json { ignoreUnknownKeys = true }
        
        val parsed = try {
            json.decodeFromString<List<PythonPackageSurrogate>>(raw)
        } catch (_: SerializationException) {
            return null
        }
        
        return parsed.map { PythonPackage(it) }
    }
    
    /**
     * @see com.jetbrains.python.packaging.management.PythonPackageManager.refreshPaths
     */
    private suspend fun refreshPaths() = writeAction {
        val (async, recursive, reloadChildren) = Triple(true, true, true)
        
        VfsUtil.markDirtyAndRefresh(
            async, recursive, reloadChildren,
            *sdk.rootProvider.getFiles(OrderRootType.CLASSES)
        )
        PythonSdkUpdater.scheduleUpdate(sdk, project)
    }
    
    private suspend fun refreshAndReload() = run {
        refreshPaths()
        reloadPackages()
    }
    
    override suspend fun installPackage(specification: PythonPackageSpecification) =
        uv.add(specification).runAndGetResult()
    
    override suspend fun updatePackage(specification: PythonPackageSpecification) =
        uv.upgrade(specification).runAndGetResult()
    
    override suspend fun uninstallPackage(pkg: PythonPackage) =
        uv.remove(pkg.name).runAndGetResult()
    
    private suspend fun Command.runAndGetResult(): Result<List<PythonPackage>> {
        val output = project.runInBackground(this)
        
        return when {
            output.completedAbnormally -> Result.failure(UVReportedError(output))
            else -> refreshAndReload()
        }
    }
    
}
