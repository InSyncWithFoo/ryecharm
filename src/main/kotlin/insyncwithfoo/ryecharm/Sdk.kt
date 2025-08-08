package insyncwithfoo.ryecharm

import com.intellij.openapi.projectRoots.Sdk
import com.jetbrains.python.sdk.uv.UvSdkAdditionalData
import java.nio.file.Path


/**
 * Attempt to convert the result of [Sdk.getHomePath] to a [Path].
 * 
 * @see toPathOrNull
 */
internal val Sdk.path: Path?
    get() = homePath?.toPathOrNull()


/**
 * Whether this SDK is of uv flavor.
 * 
 * The attached additional data of an uv SDK
 * is always of type [UvSdkAdditionalData].
 * This class is internal, however,
 * and so it cannot be referred to directly.
 * This property thus perform a qualified name check
 * as a workaround.
 * 
 * @see com.jetbrains.python.sdk.uv.isUv
 */
internal val Sdk.isUV: Boolean
    get() {
        val additionalData = sdkAdditionalData ?: return false
        
        return additionalData::class.qualifiedName == "com.jetbrains.python.sdk.uv.UvSdkAdditionalData"
    }
