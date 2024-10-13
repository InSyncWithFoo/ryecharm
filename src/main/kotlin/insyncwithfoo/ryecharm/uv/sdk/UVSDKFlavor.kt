package insyncwithfoo.ryecharm.uv.sdk

import com.jetbrains.python.sdk.PythonSdkAdditionalData
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.flavors.CPythonSdkFlavor
import com.jetbrains.python.sdk.flavors.PyFlavorData
import com.jetbrains.python.sdk.flavors.VirtualEnvSdkFlavor
import insyncwithfoo.ryecharm.UVIcons
import java.nio.file.Path


/**
 * @see com.jetbrains.python.sdk.flavors.PythonSdkFlavor
 */
internal object UVSDKFlavor : CPythonSdkFlavor<PyFlavorData.Empty>() {
    
    /**
     * The icon to be used in the interpreter dropdown in
     * **Settings** | **Project** | **Python Interpreter**.
     * 
     * Also used by [UVSDKProvider.getSdkIcon].
     * 
     * Size: 16&times;16
     */
    override fun getIcon() = UVIcons.TINY_16
    
    /**
     * Return the class to be used in the deserialization process
     * at [PythonSdkAdditionalData.setFlavorFromConfig].
     * 
     * At the moment, there is nothing special to be stored,
     * so this simply returns [PyFlavorData.Empty].
     * 
     * This override is required, though not technically.
     */
    override fun getFlavorDataClass(): Class<PyFlavorData.Empty> =
        PyFlavorData.Empty::class.java
    
    /**
     * Verify if [path] leads to a valid interpreter.
     * 
     * This method is currently a copy of
     * [VirtualEnvSdkFlavor.isValidSdkPath].
     */
    // TODO: Delegate this to uv instead.
    override fun isValidSdkPath(path: Path): Boolean {
        return super.isValidSdkPath(path) && PythonSdkUtil.getVirtualEnvRoot(path.toString()) != null
    }
    
}
