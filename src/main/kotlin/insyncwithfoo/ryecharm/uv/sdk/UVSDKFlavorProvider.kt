package insyncwithfoo.ryecharm.uv.sdk

import com.jetbrains.python.sdk.flavors.PythonFlavorProvider


/**
 * Exists only to provide [UVSDKFlavor].
 */
internal class UVSDKFlavorProvider : PythonFlavorProvider {
    
    override fun getFlavor(platformIndependent: Boolean) = UVSDKFlavor
    
}
