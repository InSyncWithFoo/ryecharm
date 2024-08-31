package insyncwithfoo.ryecharm.ruff.documentation.noqa

import com.intellij.model.Pointer
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiComment
import com.intellij.psi.createSmartPointer
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.markdownToHTML
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toDocumentationResult


@Suppress("UnstableApiUsage")
internal class NoqaCodeDocumentationTarget(
    private val element: PsiComment,
    private val noqaComment: NoqaComment,
    private val offset: Int
) : DocumentationTarget {
    
    private val project: Project
        get() = element.project
    
    // This doesn't seem to do anything.
    override fun computePresentation() =
        TargetPresentation.builder("").presentation()
    
    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPointer = element.createSmartPointer()
        
        return Pointer {
            elementPointer.dereference()?.let {
                NoqaCodeDocumentationTarget(it, noqaComment, offset)
            }
        }
    }
    
    override fun computeDocumentation(): DocumentationResult? {
        val noqaCode = noqaComment.findNoqaCodeAtOffset(offset) ?: return null
        return computeDocumentation(noqaCode)
    }
    
    private fun computeDocumentation(noqaCode: NoqaCode) = DocumentationResult.asyncDocumentation {
        project.getDocumentation(noqaCode)?.toDocumentationResult()
    }
    
    private suspend fun Project.getDocumentation(noqaCode: NoqaCode): HTML? {
        val markdownDocumentation = getMarkdownDocumentation(noqaCode) ?: return null
        
        return readAction {
            markdownToHTML(markdownDocumentation)
        }
    }
    
    private suspend fun Project.getMarkdownDocumentation(noqaCode: String): Markdown? {
        val ruff = this.ruff ?: return null
        val command = ruff.rule(noqaCode)
        
        val output = ProgressContext.IO.compute {
            runInBackground(command)
        }
        
        if (output.isTimeout) {
            processTimeout(command)
            return null
        }
        
        if (output.isCancelled || !output.isSuccessful) {
            return null
        }
        
        return output.stdout
    }
    
}
