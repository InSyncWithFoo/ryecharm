package insyncwithfoo.ryecharm.ruff.documentation.rules

import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.createSmartPointer
import insyncwithfoo.ryecharm.ruff.documentation.getDocumentationForRule
import insyncwithfoo.ryecharm.ruff.documentation.noqa.RuleCode
import insyncwithfoo.ryecharm.toDocumentationResult
import org.toml.TomlIcons
import org.toml.lang.psi.TomlLiteral


@Suppress("UnstableApiUsage")
internal class RuffRuleDocumentationTarget(
    private val element: TomlLiteral,
    private val rule: RuleCode,
    private val fileName: String
) : DocumentationTarget {
    
    // This doesn't seem to do anything, despite being called.
    override fun computePresentation() =
        TargetPresentation.builder(fileName)
            .presentableText(fileName)
            .icon(TomlIcons.TomlFile)
            .presentation()
    
    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPointer = element.createSmartPointer()
        
        return Pointer {
            elementPointer.dereference()?.let {
                RuffRuleDocumentationTarget(it, rule, fileName)
            }
        }
    }
    
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        element.project.getDocumentationForRule(rule)?.toDocumentationResult()
    }
    
}
