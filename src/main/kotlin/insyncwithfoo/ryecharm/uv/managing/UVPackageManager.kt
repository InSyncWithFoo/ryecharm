package insyncwithfoo.ryecharm.uv.managing

import com.intellij.execution.ExecutionException
import com.intellij.execution.RunCanceledByUserException
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.bridge.PythonPackageManagementServiceBridge
import com.jetbrains.python.packaging.common.PythonPackage
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import com.jetbrains.python.packaging.pip.PipBasedPackageManager
import com.jetbrains.python.packaging.pip.PipRepositoryManager
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.parsePipListOutput


private typealias Stdout = String


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
internal class UVPackageManager(private val uv: UV, project: Project, sdk: Sdk) : PipBasedPackageManager(project, sdk) {
    
    override val repositoryManager by lazy { PipRepositoryManager(project, sdk) }
    
    override suspend fun reloadPackagesCommand(): Result<List<PythonPackage>> {
        val command = uv.pipList()
        val output = project.runInBackground(command)
        val packages = parsePipListOutput(output.stdout)
        
        return when {
            output.isCancelled -> Result.failure(RunCanceledByUserException())
            !output.isSuccessful || packages == null -> Result.failure(UVReportedError(output))
            else -> Result.success(packages).also { installedPackages = packages }
        }
    }
    
    /**
     * @param options Often (always?) empty
     */
    override suspend fun installPackageCommand(
        specification: PythonPackageSpecification,
        options: List<String>
    ) =
        uv.add(specification).runAndGetResult()
    
    override suspend fun updatePackageCommand(specification: PythonPackageSpecification) =
        uv.upgrade(specification).runAndGetResult()
    
    override suspend fun uninstallPackageCommand(pkg: PythonPackage) =
        uv.remove(pkg.name).runAndGetResult()
    
    private suspend fun Command.runAndGetResult(): Result<Stdout> {
        val output = project.runInBackground(this)
        
        return when {
            output.isCancelled -> Result.failure(RunCanceledByUserException())
            !output.isSuccessful -> Result.failure(UVReportedError(output))
            else -> Result.success(output.stdout)
        }
    }
    
}
