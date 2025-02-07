package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.getLastModifiedTime
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
    this.takeIf { it.toFile().exists() }


internal fun Path.removeExtension() =
    when {
        parent == null -> nameWithoutExtension.toPathOrNull()
        else -> parent / nameWithoutExtension
    }


internal fun Path.findExecutableChild(name: String) =
    resolve(name.toOSDependentFileName()).toNullIfNotExists()


internal fun findExecutableInPath(name: String) =
    PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS(name)?.toPath()
