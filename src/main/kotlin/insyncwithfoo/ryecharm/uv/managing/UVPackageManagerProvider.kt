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
        
        if (!sdk.isUV && !project.uvConfigurations.packageManaging) {
            return null
        }
        
        return UVPackageManager(uv, project, sdk)
    }
    
}
