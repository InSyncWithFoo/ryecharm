package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPDiagnosticFeature
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.codeAsString
import insyncwithfoo.ryecharm.ruff.diagnosticIsForFile
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import insyncwithfoo.ryecharm.ruff.isForSyntaxError
import insyncwithfoo.ryecharm.ruff.isRuffDisableRuleComment
import insyncwithfoo.ryecharm.ruff.isRuffFixViolation
import org.eclipse.lsp4j.Diagnostic


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.DiagnosticsSupport
 */
@Suppress("UnstableApiUsage")
internal class DiagnosticFeature : LSPDiagnosticFeature() {
    
    private val configurations = project.ruffConfigurations
    
    override fun isEnabled(file: PsiFile) =
        configurations.linting
    
    override fun getTooltip(diagnostic: Diagnostic): String {
        val rule = diagnostic.codeAsString
        val message = diagnostic.message
        
        return configurations.tooltipFormat % Pair(message, rule)
    }
    
    override fun getHighlightSeverity(diagnostic: Diagnostic) =
        super.getHighlightSeverity(diagnostic)
            .takeUnless { diagnostic.isForSyntaxError && !configurations.showSyntaxErrors }
    
    override fun createAnnotation(
        diagnostic: Diagnostic,
        document: Document,
        fixes: List<IntentionAction>,
        holder: AnnotationHolder
    ) {
        val textRange = document.getOffsetRange(diagnostic.range)
        val severity = getHighlightSeverity(diagnostic) ?: return
        val message = getMessage(diagnostic)
        val builder = holder.newAnnotation(severity, message)
        
        builder.tooltip(getTooltip(diagnostic))
        
        when (textRange.diagnosticIsForFile && configurations.fileLevelBanner) {
            true -> builder.fileLevel()
            false -> builder.range(textRange)
        }
        
        getProblemHighlightType(diagnostic.tags)?.let {
            builder.highlightType(it)
        }
        
        fixes.filter { it.isAllowed }.forEach { builder.withFix(it) }
        
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
