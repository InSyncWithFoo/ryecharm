package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.platform.lsp.api.customization.LspDiagnosticsSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import org.eclipse.lsp4j.Diagnostic


private val Diagnostic.codeAsString: String?
    get() = code?.get() as String?


/**
 * @see isRuffDisableRuleComment
 */
private val IntentionAction.isRuffFixViolation: Boolean
    get() = !this.isRuffDisableRuleComment


/**
 * @see isRuffDisableRuleComment
 */
private val IntentionAction.isRuffDisableRuleComment: Boolean
    get() = text.endsWith("Disable for this line")


@Suppress("UnstableApiUsage")
internal class DiagnosticsSupport(project: Project) : LspDiagnosticsSupport() {
    
    private val configurations = project.ruffConfigurations
    
    override fun getTooltip(diagnostic: Diagnostic): String {
        val rule = diagnostic.codeAsString
        val message = diagnostic.message
        
        return configurations.tooltipFormat % Pair(message, rule)
    }
    
    override fun createAnnotation(
        holder: AnnotationHolder,
        diagnostic: Diagnostic,
        textRange: TextRange,
        quickFixes: List<IntentionAction>
    ) {
        val filteredQuickFixes = quickFixes.filter { it.isAllowed }
        
        super.createAnnotation(holder, diagnostic, textRange, filteredQuickFixes)
    }
    
    private val IntentionAction.isAllowed: Boolean
        get() = when {
            !configurations.quickFixes -> false
            this.isRuffDisableRuleComment && !configurations.disableRuleComment -> false
            this.isRuffFixViolation && !configurations.fixViolation -> false
            else -> true
        }
    
}
