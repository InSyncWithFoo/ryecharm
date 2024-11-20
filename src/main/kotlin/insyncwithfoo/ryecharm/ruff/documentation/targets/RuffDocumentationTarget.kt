package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.model.Pointer
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.createSmartPointer


@Suppress("UnstableApiUsage")
internal abstract class RuffDocumentationTarget : DocumentationTarget {
    
    abstract val element: PsiElement
    
    protected val project: Project
        get() = element.project
    
    protected abstract fun fromDereferenced(element: PsiElement): RuffDocumentationTarget
    
    // This doesn't seem to do anything, despite being called.
    override fun computePresentation() =
        TargetPresentation.builder("").presentation()
    
    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPointer = element.createSmartPointer()
        
        return Pointer {
            elementPointer.dereference()?.let { fromDereferenced(it) }
        }
    }
    
}
