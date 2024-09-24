package insyncwithfoo.ryecharm.uv.managing

import com.jetbrains.python.PyBundle
import com.jetbrains.python.packaging.common.PythonPackageDetails
import com.jetbrains.python.packaging.common.PythonSimplePackageDetails
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.toolwindow.actions.PythonPackageInstallAction
import com.jetbrains.python.packaging.toolwindow.actions.PythonPackagingToolwindowActionProvider
import com.jetbrains.python.packaging.toolwindow.actions.SimplePythonPackageInstallAction


/**
 * @see com.jetbrains.python.packaging.pip.PipPackagingToolwindowActionProvider
 */
internal class UVPackagingToolwindowActionProvider : PythonPackagingToolwindowActionProvider {
    
    // TODO: Support uv install --dev et al.
    /**
     * Without this, the *Install* links in the *PyPI* section
     * of the *Python Packages* toolwindow cease to be functional
     * (no package versions popup, etc.).
     */
    @Suppress("UnstableApiUsage")
    override fun getInstallActions(
        details: PythonPackageDetails,
        packageManager: PythonPackageManager
    ): List<PythonPackageInstallAction>? {
        if (packageManager !is UVPackageManager || details !is PythonSimplePackageDetails) {
            return null
        }
        
        val message = PyBundle.message("python.packaging.button.install.package")
        val installAction = SimplePythonPackageInstallAction(message, packageManager.project)
        
        return listOf(installAction)
    }
    
}
