package insyncwithfoo.ryecharm

import com.intellij.openapi.projectRoots.Sdk
import insyncwithfoo.ryecharm.uv.sdk.UVSDKAdditionalData
import java.nio.file.Path


internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()


internal val Sdk.isUV: Boolean
    get() = sdkAdditionalData is UVSDKAdditionalData
