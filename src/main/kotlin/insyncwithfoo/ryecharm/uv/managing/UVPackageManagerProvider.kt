package insyncwithfoo.ryecharm.uv.managing

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.management.PythonPackageManagerProvider
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.isUV
import insyncwithfoo.ryecharm.uv.commands.uv


/**
 * Exists only to provide [UVPackageManager].
 */
@Suppress("UnstableApiUsage")
internal class UVPackageManagerProvider : PythonPackageManagerProvider {
    
    override fun createPackageManagerForSdk(project: Project, sdk: Sdk): PythonPackageManager? {
        val uv = project.uv ?: return null
        val configurations = project.uvConfigurations
        
        val useUV = when {
            sdk.isUV -> configurations.packageManaging
            else -> configurations.packageManagingNonUVProjects
        }
        
        return when {
            useUV -> UVPackageManager(uv, project, sdk)
            else -> null
        }
    }
    
}
