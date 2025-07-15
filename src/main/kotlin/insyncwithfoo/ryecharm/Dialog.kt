package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper


internal abstract class Dialog(project: Project?) : DialogWrapper(project) {
    
    internal var okButtonText: String
        @Deprecated("The getter must not be used.", level = DeprecationLevel.HIDDEN)
        get() = throw RuntimeException()
        set(value) = setOKButtonText(value)
    
    internal var widthAndHeight: Pair<Int, Int>
        @Deprecated("The getter must not be used.", level = DeprecationLevel.HIDDEN)
        get() = throw RuntimeException()
        set(value) {
            val (width, height) = value
            setSize(width, height)
        }
    
}
