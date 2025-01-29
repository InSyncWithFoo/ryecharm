package insyncwithfoo.ryecharm

import com.intellij.openapi.projectRoots.Sdk
import java.nio.file.Path


internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()
