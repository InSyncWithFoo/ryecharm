package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.customization.LspDiagnosticsSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.codeAsString
import insyncwithfoo.ryecharm.ruff.diagnosticIsForFile
import insyncwithfoo.ryecharm.ruff.getFormattedTooltip
import insyncwithfoo.ryecharm.ruff.isForSyntaxError
import insyncwithfoo.ryecharm.ruff.isRuffDisableRuleComment
import insyncwithfoo.ryecharm.ruff.isRuffFixViolation
import org.eclipse.lsp4j.Diagnostic


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp4ij.DiagnosticFeature
 */
internal class DiagnosticsSupport(project: Project) : LspDiagnosticsSupport() {
    
    private val configurations = project.ruffConfigurations
    
    override fun shouldAskServerForDiagnostics(file: VirtualFile) =
        configurations.letNativeClientPullDiagnostics
    
    override fun getTooltip(diagnostic: Diagnostic): String {
        val rule = diagnostic.codeAsString
        val message = diagnostic.message
        
        return configurations.getFormattedTooltip(message, rule)
    }
    
    override fun getHighlightSeverity(diagnostic: Diagnostic) =
        super.getHighlightSeverity(diagnostic)
            .takeUnless { diagnostic.isForSyntaxError && !configurations.showSyntaxErrors }
    
    /**
     * @see LspDiagnosticsSupport.createAnnotation
     */
    override fun createAnnotation(
        holder: AnnotationHolder,
        diagnostic: Diagnostic,
        textRange: TextRange,
        quickFixes: List<IntentionAction>
    ) {
        val severity = getHighlightSeverity(diagnostic) ?: return
        val message = getMessage(diagnostic)
        val builder = holder.newAnnotation(severity, message)
        
        builder.tooltip(getTooltip(diagnostic))
        
        when (textRange.diagnosticIsForFile && configurations.fileLevelBanner) {
            true -> builder.fileLevel()
            false -> builder.range(textRange)
        }
        
        getSpecialHighlightType(diagnostic)?.let {
            builder.highlightType(it)
        }
        
        quickFixes.filter { it.isAllowed }.forEach { builder.withFix(it) }
        
        builder.create()
    }
    
    private val IntentionAction.isAllowed: Boolean
        get() = when {
            !configurations.quickFixes -> false
            this.isRuffDisableRuleComment && !configurations.disableRuleComment -> false
            this.isRuffFixViolation && !configurations.fixViolation -> false
            else -> true
        }
    
}
