package insyncwithfoo.ryecharm.uv.sdk

import com.jetbrains.python.sdk.PythonSdkAdditionalData
import com.jetbrains.python.sdk.flavors.PyFlavorAndData
import com.jetbrains.python.sdk.flavors.PyFlavorData
import insyncwithfoo.ryecharm.uv.sdk.UVSDKAdditionalData.Companion.IS_UV
import org.jdom.Element

/**
 * Additional data for uv-created SDKs.
 * 
 * Currently only consists of an extra
 * [IS_UV] boolean attribute.
 */
internal class UVSDKAdditionalData : PythonSdkAdditionalData {
    
    constructor() : super(PyFlavorAndData(PyFlavorData.Empty, UVSDKFlavor))
    constructor(data: PythonSdkAdditionalData? = null) : super(data ?: PythonSdkAdditionalData())
    
    override fun save(element: Element) {
        super.save(element)
        element.setAttribute(IS_UV, "true")
    }
    
    companion object {
        private const val IS_UV = "IS_UV"
        
        fun load(element: Element) = when {
            element.getAttributeValue(IS_UV) != "true" -> null
            else -> UVSDKAdditionalData().apply { load(element) }
        }
    }
    
}
