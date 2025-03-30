package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.canBeLintedByRuff
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.getFormattedTooltip
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import java.nio.file.Path


private val Project.inspectionManager: InspectionManager
    get() = InspectionManager.getInstance(this)


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
        if (!file.canBeLintedByRuff) {
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
        val document = file.viewProvider.document
        
        return InitialInfo(project, configurations, ruff, document.text, path)
    }
    
    override fun doAnnotate(collectedInfo: InitialInfo?): AnnotationResult? {
        val (project, configurations, ruff, text, path) = collectedInfo ?: return null
        
        val command = ruff.check(text, path, allFixable = configurations.considerAllFixable)
        val results = project.runCheckCommand(command) ?: return null
        
        return AnnotationResult(configurations, results)
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
            
            val tooltip = configurations.getFormattedTooltip(diagnostic.message, diagnostic.code)
            val range = document.getOffsetRange(diagnostic.oneBasedRange)
            val noqaOffset = diagnostic.getNoqaOffset(document)
            
            builder.needsUpdateOnTyping()
            builder.tooltip(tooltip)
            
            when (diagnostic.isForFile && configurations.fileLevelBanner) {
                true -> builder.fileLevel()
                false -> builder.range(range)
            }
            
            diagnostic.makeFixViolationFix(configurations)?.let {
                builder.registerQuickFix(file, message, it)
            }
            
            diagnostic.makeFixSimilarViolationsFixes(configurations)?.let { (safe, unsafe) ->
                builder.registerQuickFix(file, message, safe)
                builder.registerQuickFix(file, message, unsafe)
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
