package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.TooltipFormat
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.ZeroBasedIndex
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import insyncwithfoo.ryecharm.ruff.toZeroBased
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unknownError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.nio.file.Path


private val Project.inspectionManager: InspectionManager
    get() = InspectionManager.getInstance(this)


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.isForSyntaxError
 */
private val Diagnostic.isForSyntaxError: Boolean
    get() = code == null


private val Diagnostic.isUnsuppressable: Boolean
    get() = code in listOf(
        "PGH004"  // blanket-noqa
    )


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.diagnosticIsForFile
 */
private val Diagnostic.isForFile: Boolean
    get() = oneBasedRange == OneBasedRange.FILE_LEVEL


private fun Diagnostic.getFormattedTooltip(format: TooltipFormat) =
    format % Pair(message, code)


private fun Document.rangeIsAfterEndOfLine(range: TextRange): Boolean {
    val rangeIsAtEOF = range.endOffset == textLength
    val rangeIsAtLineBreak = charsSequence.getOrNull(range.startOffset) == '\n'
    
    return range.isEmpty && (rangeIsAtEOF || rangeIsAtLineBreak)
}


internal data class InitialInfo(
    val project: Project,
    val configurations: RuffConfigurations,
    val ruff: Ruff,
    val text: String,
    val path: Path
)


internal data class AnnotationResult(
    val configurations: RuffConfigurations,
    val results: List<Diagnostic>
)


internal class RuffAnnotator : ExternalAnnotator<InitialInfo, AnnotationResult>(), DumbAware {
    
    private val highlightSeverity = HighlightSeverity.WARNING
    private val problemHighlightType = ProblemHighlightType.WARNING
    
    override fun getPairedBatchInspectionShortName() =
        RuffInspection.SHORT_NAME
    
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): InitialInfo? {
        if (!file.isSupportedByRuff) {
            return null
        }
        
        val project = file.project
        val configurations = project.ruffConfigurations
        
        if (configurations.runningMode != RunningMode.COMMAND_LINE) {
            return null
        }
        
        if (!configurations.linting) {
            return null
        }
        
        val ruff = project.ruff ?: return null
        val path = file.virtualFile.toNioPathOrNull() ?: return null
        
        return InitialInfo(project, configurations, ruff, file.text, path)
    }
    
    @Suppress("UnstableApiUsage")
    override fun doAnnotate(collectedInfo: InitialInfo?): AnnotationResult? {
        val (project, configurations, ruff, text, path) = collectedInfo ?: return null
        
        val command = ruff.check(text, path)
        val output = runBlockingCancellable {
            project.runInBackground(command)
        }
        val results = parseCheckOutput(output.stdout)
        
        if (output.isCancelled) {
            return null
        }
        
        if (output.isTimeout) {
            project.processTimeout(command)
            return null
        }
        
        if (!output.isSuccessful || results == null) {
            project.unknownError(command, output)
            return null
        }
        
        return AnnotationResult(configurations, results)
    }
    
    private fun parseCheckOutput(raw: String) = try {
        Json.decodeFromString<List<Diagnostic>>(raw)
    } catch (_: SerializationException) {
        null
    }
    
    override fun apply(file: PsiFile, annotationResult: AnnotationResult?, holder: AnnotationHolder) {
        val (configurations, diagnostics) = annotationResult ?: return
        val document = file.viewProvider.document ?: return
        
        val diagnosticsPossiblyWithoutSyntaxErrors = when {
            configurations.showSyntaxErrors -> diagnostics
            else -> diagnostics.filter { !it.isForSyntaxError }
        }
        
        diagnosticsPossiblyWithoutSyntaxErrors.forEach { diagnostic ->
            val message = diagnostic.message
            val builder = holder.newAnnotation(highlightSeverity, message)
            
            val tooltip = diagnostic.getFormattedTooltip(configurations.tooltipFormat)
            val range = document.getOffsetRange(diagnostic.oneBasedRange)
            val noqaOffset = when (diagnostic.noqaRow) {
                null -> range.startOffset
                else -> document.getLineStartOffset(diagnostic.noqaRow.toZeroBased())
            }
            
            builder.needsUpdateOnTyping()
            builder.tooltip(tooltip)
            
            when (diagnostic.isForFile && configurations.fileLevelBanner) {
                true -> builder.fileLevel()
                false -> builder.range(range)
            }
            
            diagnostic.makeFixViolationFix(configurations)?.let {
                builder.registerQuickFix(file, message, it)
            }
            
            diagnostic.makeDisableRuleCommentFix(configurations, noqaOffset)?.let {
                builder.registerQuickFix(file, message, it)
            }
            
            if (document.rangeIsAfterEndOfLine(range)) {
                builder.afterEndOfLine()
            }
            
            builder.create()
        }
    }
    
    private fun Diagnostic.makeFixViolationFix(configurations: RuffConfigurations) =
        when {
            !configurations.quickFixes || !configurations.fixViolation -> null
            fix == null || code == null -> null
            else -> RuffFixViolation(code, fix)
        }
    
    private fun Diagnostic.makeDisableRuleCommentFix(configurations: RuffConfigurations, offset: ZeroBasedIndex) =
        when {
            !configurations.quickFixes || !configurations.disableRuleComment -> null
            code == null || this.isUnsuppressable -> null
            else -> RuffDisableRuleComment(code, offset)
        }
    
    private fun AnnotationBuilder.registerQuickFix(file: PsiFile, message: String, fix: LocalQuickFix) {
        val problemDescriptor = file.createProblemDescriptor(message, fix)
        newLocalQuickFix(fix, problemDescriptor).registerFix()
    }
    
    private fun PsiFile.createProblemDescriptor(message: String, fix: LocalQuickFix): ProblemDescriptor {
        val onTheFly = true
        
        return project.inspectionManager
            .createProblemDescriptor(this, message, fix, problemHighlightType, onTheFly)
    }
    
}
