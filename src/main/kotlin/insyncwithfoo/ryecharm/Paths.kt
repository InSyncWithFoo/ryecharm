package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import java.io.File
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
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


/**
 * Attempt to convert the string to a normalized [Path],
 * then call [toNullIfNotExists] on it.
 */
internal fun String.toPathIfItExists() =
    this.toPathOrNull()?.normalize()?.toNullIfNotExists()


/**
 * Append `.exe` to the string if the current system is Windows.
 */
internal fun String.toOSDependentFileName() = when {
    SystemInfo.isWindows -> "$this.exe"
    else -> this
}


/**
 * Return the path unchanged if it is occupied.
 * Otherwise, return `null`.
 * 
 * This necessarily uses [File.exists] rather than [Path.exists],
 * which itself calls [Files.exists].
 */
internal fun Path.toNullIfNotExists() =
    this.takeIf { it.toFile().exists() }


/**
 * Find a direct child of the current path
 * whose name without extension is as given.
 * 
 * Return null for non-directories.
 */
internal fun Path.findChildIgnoringExtension(childNameWithoutExtension: String) = 
    this.takeIf { it.isDirectory() }
        ?.listDirectoryEntries()
        ?.find { it.nameWithoutExtension == childNameWithoutExtension }


/**
 * Return the path constructed by joining the current one with [name],
 * if it is occupied.
 */
internal fun Path.findExecutableChild(name: String) =
    resolve(name.toOSDependentFileName()).toNullIfNotExists()


/**
 * Look for [name] in PATH.
 */
internal fun findExecutableInPath(name: String) =
    PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS(name)?.toPath()
