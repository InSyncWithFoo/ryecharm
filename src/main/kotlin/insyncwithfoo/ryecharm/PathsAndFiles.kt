package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension


private val Path.isEmpty: Boolean
    get() = this.toString() == ""


/**
 * An epoch timestamp (in seconds) representing the moment
 * the file or directory at the path was last modified.
 */
internal val Path.lastModified: Long
    get() = getLastModifiedTime().toInstant().epochSecond


/**
 * Parse the string and return a [Path]
 * if the new [Path] is not blank or invalid.
 */
internal fun String.toPathOrNull() =
    try {
        Path.of(this).takeUnless { it.isEmpty }
    } catch (_: InvalidPathException) {
        null
    }


internal fun String.toPathIfItExists() =
    this.toPathOrNull()?.normalize()?.toNullIfNotExists()


internal fun String.toOSDependentFileName() = when {
    SystemInfo.isWindows -> "$this.exe"
    else -> this
}


internal fun Path.toNullIfNotExists() =
    this.takeIf { it.exists() }


private fun Path.directoryIsEmpty() = listDirectoryEntries().isEmpty()


internal fun Path.isNonEmptyDirectory() = isDirectory() && !directoryIsEmpty()


internal fun Path.removeExtension() =
    when {
        parent == null -> nameWithoutExtension.toPathOrNull()
        else -> parent / nameWithoutExtension
    }


internal fun Path.findExecutableChild(name: String) =
    resolve(name.toOSDependentFileName()).toNullIfNotExists()


internal fun findExecutableInPath(name: String): Path? {
    return PathEnvironmentVariableUtil.findInPath(name.toOSDependentFileName())?.toPath()
}


internal val VirtualFile.isRyeConfigToml: Boolean
    get() = parent?.name == ".rye" && name == "config.toml"


internal val VirtualFile.isPyprojectToml: Boolean
    get() = name == "pyproject.toml"


internal val VirtualFile.isUVLock: Boolean
    get() = name == "uv.lock"


internal val VirtualFile.isUVToml: Boolean
    get() = name == "uv.toml"


internal val VirtualFile.isRuffToml: Boolean
    get() = name == "ruff.toml" || name == ".ruff.toml"


// https://github.com/InSyncWithFoo/ryecharm/issues/5
private val PsiFile.isReST: Boolean
    get() = virtualFile?.extension == "rst"


// TODO: .ipynb / Allow configuring what files
internal fun VirtualFile.isSupportedByRuff(project: Project? = null): Boolean {
    return extension == "py" || extension == "pyi"
}


internal val PsiFile.isSupportedByRuff: Boolean
    get() = this is PyFile && !this.isReST || virtualFile?.isSupportedByRuff(project) == true
