package insyncwithfoo.ryecharm

import com.intellij.openapi.projectRoots.Sdk
import java.nio.file.Path


/**
 * Attempt to convert the result of [Sdk.getHomePath] to a [Path].
 * 
 * @see toPathOrNull
 */
internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()
