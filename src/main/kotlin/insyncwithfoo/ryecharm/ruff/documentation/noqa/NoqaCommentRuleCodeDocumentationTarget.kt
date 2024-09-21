package insyncwithfoo.ryecharm.ruff.documentation.noqa

import com.intellij.model.Pointer
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiComment
import com.intellij.psi.createSmartPointer
import insyncwithfoo.ryecharm.ruff.documentation.getDocumentationForRule
import insyncwithfoo.ryecharm.toDocumentationResult


@Suppress("UnstableApiUsage")
internal class NoqaCommentRuleCodeDocumentationTarget(
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
                NoqaCommentRuleCodeDocumentationTarget(it, noqaComment, offset)
            }
        }
    }
    
    override fun computeDocumentation(): DocumentationResult? {
        val ruleCode = noqaComment.findRuleCodeAtOffset(offset) ?: return null
        
        return DocumentationResult.asyncDocumentation {
            project.getDocumentationForRule(ruleCode)?.toDocumentationResult()
        }
    }
    
}
