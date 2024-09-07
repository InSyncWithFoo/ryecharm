@file:Suppress("removal", "DEPRECATION")

package insyncwithfoo.ryecharm.uv.sdk

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.UserDataHolder
import com.jetbrains.python.inspections.PyInterpreterInspection
import com.jetbrains.python.inspections.PyPackageRequirementsInspection
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.packaging.ui.PyPackageManagementService
import com.jetbrains.python.sdk.PyInterpreterInspectionQuickFixData
import com.jetbrains.python.sdk.PySdkProvider
import com.jetbrains.python.sdk.add.PyAddNewEnvPanel
import com.jetbrains.python.sdk.add.v2.PythonAddNewEnvironmentPanel
import insyncwithfoo.ryecharm.isUV
import insyncwithfoo.ryecharm.uv.managing.UVPackageManager
import org.jdom.Element


/**
 * Responsible for various actions that [PySdkProvider] supports.
 *
 * The interface is supposed to be deprecated/obsolete.
 * Regardless, there have yet to be a clean replacement.
 */
@Suppress("UnstableApiUsage")
internal class UVSDKProvider : PySdkProvider {
    
    /**
     * According to the code at [PyInterpreterInspection.Visitor.visitPyFile],
     * this method is supposed to provide a quick fix that
     * associates an SDK with a module when:
     *
     * * The `PyFile` being visited belongs to a module,
     *   or the project has only one module
     *   (as guessed by [PyInterpreterInspection.guessModule]).
     * * That module has a Python SDK.
     * * That SDK isn't associated with another module or module path.
     */
    // TODO: Support this
    override fun createEnvironmentAssociationFix(
        module: Module,
        sdk: Sdk,
        isPyCharm: Boolean,
        associatedModulePath: String?
    ): PyInterpreterInspectionQuickFixData? {
        return null
    }
    
    /**
     * According to the code at [PyPackageRequirementsInspection.Visitor.visitPyFile],
     * this method is supposed to return a quick fix that
     * install missing packages when:
     *
     * * The visit call happens when no other package operations is happening.
     * * The `PyFile` being visited belongs to a module
     *   (as returned by [ModuleUtilCore.findModuleForPsiElement]).
     * * That module has a Python SDK.
     * * There are at least one missing package.
     */
    // TODO: Support this
    override fun createInstallPackagesQuickFix(module: Module): LocalQuickFix? {
        return null
    }
    
    /**
     * This method is called at:
     * * [com.jetbrains.python.newProject.NewEnvironmentStep.initSteps]
     * * [com.jetbrains.python.newProject.steps.PyAddNewEnvironmentPanel.createPanels]
     * 
     * Both of which appear to be dead code on PyCharm.
     * They have possibly been superseded by [PythonAddNewEnvironmentPanel]
     * and other classes in the same `sdk.add.v2` subpackage,
     * none of which makes use of "old" extension points.
     * On IntelliJ IDEA, however, this is called
     * when its modular new project panel is opened.
     * 
     * IDEA is not a prioritized target, so this method is
     * currently only overridden here for documentation purposes.
     */
    // TODO: Support this.
    override fun createNewEnvironmentPanel(
        project: Project?,
        module: Module?,
        existingSdks: List<Sdk>,
        newProjectPath: String?,
        context: UserDataHolder
    ): PyAddNewEnvPanel {
        throw RuntimeException()
    }
    
    /**
     * The string to be appended to the suggested SDK name
     * when displayed in the status bar.
     *
     * For example: A default `venv` environments may have
     * its rendered label reads `Python 3.12 (project)`.
     * If this method of it were to return `sdk.versionString`,
     * it would read `Python 3.12 (project) [Python 3.12.4]`.
     *
     * @see com.jetbrains.python.sdk.name
     */
    override fun getSdkAdditionalText(sdk: Sdk) = null
    
    /**
     * The icon to be used in the *Python Interpreter* popup,
     * which is triggered by clicking the corresponding status bar cell.
     *
     * Size: 16&times;16
     *
     * @see com.jetbrains.python.sdk.icon
     */
    override fun getSdkIcon(sdk: Sdk) =
        UVSDKFlavor.icon.takeIf { sdk.isUV }
    
    override fun loadAdditionalDataForSdk(element: Element) =
        UVSDKAdditionalData.load(element)
    
    /**
     * This method is supposed to return a [PyPackageManagementService],
     * which was deprecated in favor of [PythonPackageManager].
     *
     * @see UVPackageManager
     */
    override fun tryCreatePackageManagementServiceForSdk(project: Project, sdk: Sdk): PyPackageManagementService? {
        return null
    }
    
}
