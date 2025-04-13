package insyncwithfoo.ryecharm

import com.intellij.model.Pointer
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.createSmartPointer


@Suppress("UnstableApiUsage")
internal abstract class ElementBasedDocumentationTarget : DocumentationTarget {
    
    abstract val element: PsiElement
    
    protected val project: Project
        get() = element.project
    
    protected abstract fun fromDereferenced(element: PsiElement): ElementBasedDocumentationTarget
    
    // This doesn't seem to do anything, despite being called.
    final override fun computePresentation() =
        TargetPresentation.builder("").presentation()
    
    final override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPointer = element.createSmartPointer()
        
        return Pointer {
            elementPointer.dereference()?.let { fromDereferenced(it) }
        }
    }
    
}
