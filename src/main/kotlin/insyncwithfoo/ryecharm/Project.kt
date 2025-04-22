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
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.pythonSdk
import java.nio.file.Path


private val projectManager: ProjectManager
    get() = ProjectManager.getInstance()


internal val openProjects: Sequence<Project>
    get() = projectManager.openProjects.asSequence()


/**
 * Return the so-called "default" project singleton.
 * 
 * The default project is used to
 * store default settings for new projects.
 * 
 * In extreme cases, it is used in lieu of an actual project.
 */
internal val defaultProject: Project
    get() = projectManager.defaultProject


internal val Project.rootManager: ProjectRootManager
    get() = ProjectRootManager.getInstance(this)


private val Project.moduleManager: ModuleManager
    get() = ModuleManager.getInstance(this)


internal val Project.psiDocumentManager: PsiDocumentManager
    get() = PsiDocumentManager.getInstance(this)


internal val Project.modules: Array<Module>
    get() = moduleManager.modules


/**
 * The Python SDK of this project.
 * 
 * @see pythonSdk
 */
private val Project.sdk: Sdk?
    get() = rootManager.projectSdk?.takeIf { PythonSdkUtil.isPythonSdk(it) }


internal val Project.path: Path?
    get() = guessProjectDir()?.toNioPathOrNull()?.toNullIfNotExists()
        ?: basePath?.toPathOrNull()?.toNullIfNotExists()


/**
 * Attempt to convert the result of [Sdk.getHomePath] to a [Path].
 * 
 * @see sdk
 * @see toPathIfItExists
 */
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


internal val Project.messageBusConnection: MessageBusConnection
    get() = messageBus.connect()


internal val Project.fileEditorManager: FileEditorManager
    get() = FileEditorManager.getInstance(this)


/**
 * Return the first file in the project's environment
 * whose name without extension matches the given name.
 * 
 * @see interpreterDirectory
 */
internal fun Project.findExecutableInVenv(nameWithoutExtension: String) =
    interpreterDirectory?.findChildIgnoringExtension(nameWithoutExtension)


/**
 * Attempt to open the given [virtualFile]
 * and switch focus to the new editor.
 */
internal fun Project.openFile(virtualFile: VirtualFile) {
    val focusEditor = true
    
    fileEditorManager.openFile(virtualFile, focusEditor)
}


/**
 * Create a new [LightVirtualFile] with the given [filename] and [content]
 * and open an editor for it.
 */
internal fun Project.openLightFile(filename: String, content: String) {
    val fileType = FileTypeManager.getInstance().getFileTypeByFileName(filename)
    val file = LightVirtualFile(filename, fileType, content)
    
    val openFileDescriptor = OpenFileDescriptor(this, file)
    
    fileEditorManager.openEditor(openFileDescriptor, true)
}
