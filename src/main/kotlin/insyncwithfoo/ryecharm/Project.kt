package insyncwithfoo.ryecharm

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.pythonSdk
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension


private val projectManager: ProjectManager
    get() = ProjectManager.getInstance()


internal val openProjects: Sequence<Project>
    get() = projectManager.openProjects.asSequence()


internal val defaultProject: Project
    get() = projectManager.defaultProject


internal val Project.rootManager: ProjectRootManager
    get() = ProjectRootManager.getInstance(this)


internal val Project.moduleManager: ModuleManager
    get() = ModuleManager.getInstance(this)


internal val Project.psiDocumentManager: PsiDocumentManager
    get() = PsiDocumentManager.getInstance(this)


private val Project.modules: Array<Module>
    get() = moduleManager.modules


/**
 * @see [pythonSdk]
 */
private val Project.sdk: Sdk?
    get() = rootManager.projectSdk?.takeIf { PythonSdkUtil.isPythonSdk(it) }


internal val Project.path: Path?
    get() = guessProjectDir()?.toNioPathOrNull()?.toNullIfNotExists()


internal val Project.interpreterPath: Path?
    get() = sdk?.homePath?.toPathIfItExists()


internal val Project.interpreterDirectory: Path?
    get() = interpreterPath?.parent


/**
 * Whether a project:
 * * Is not the pseudo project used to store
 *   default settings for newly created projects, and
 * * Has not been disposed of.
 */
internal val Project.isNormal: Boolean
    get() = !this.isDefault && !this.isDisposed


internal val Project.fileEditorManager: FileEditorManager
    get() = FileEditorManager.getInstance(this)


internal val Project.inspectionProfileManager: ProjectInspectionProfileManager
    get() = ProjectInspectionProfileManager.getInstance(this)


internal fun Project.findExecutableInVenv(nameWithoutExtension: String) =
    interpreterDirectory?.listDirectoryEntries()
        ?.find { it.nameWithoutExtension == nameWithoutExtension }


internal fun Project.openLightFile(fileName: String, content: String) {
    val fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName)
    val file = LightVirtualFile(fileName, fileType, content)
    
    val openFileDescriptor = OpenFileDescriptor(this, file)
    
    fileEditorManager.openEditor(openFileDescriptor, true)
}
