package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.GlobalInspectionContext
import com.intellij.codeInspection.GlobalSimpleInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptionsProcessor
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.ex.ProblemDescriptorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.common.logging.ruffLogger
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import insyncwithfoo.ryecharm.stringifyToJSON


private typealias FilePath = String
private typealias LintingResults = Map<FilePath?, List<Diagnostic>>


private val RESULTS = Key.create<LintingResults>("${RyeCharm.ID}.ruff.linting.global.results")


internal class RuffGlobalInspection : GlobalSimpleInspectionTool() {
    
    private val problemHighlightType = ProblemHighlightType.WARNING
    
    override fun isReadActionNeeded() = false
    
    override fun getSharedLocalInspectionTool() = RuffInspection()
    
    override fun worksInBatchModeOnly() = true
    
    // TODO: Fix on clean up
    override fun cleanup(project: Project) {}
    
    override fun inspectionStarted(
        manager: InspectionManager,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor
    ) {
        val project = globalContext.project
        val ruff = project.ruff ?: return
        val configurations = project.ruffConfigurations
        
        val command = ruff.checkProject(allFixable = configurations.considerAllFixable)
        val results = project.runCheckCommand(command) ?: return
        
        globalContext.putUserData(RESULTS, results.groupBy { it.filename })
    }
    
    // TODO: Make this work for injected files
    // TODO: Check each file independently
    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        problemsHolder: ProblemsHolder,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor
    ) {
        val configurations = globalContext.project.ruffConfigurations
        val document = file.viewProvider.document
        
        val results = globalContext.getUserData(RESULTS) ?: return
        val path = file.virtualFile?.toNioPathOrNull() ?: return
        val diagnostics = results[path.toString()] ?: return
        
        val diagnosticsPossiblyWithoutSyntaxErrors = when {
            configurations.showSyntaxErrors -> diagnostics
            else -> diagnostics.filter { !it.isForSyntaxError }
        }
        
        diagnosticsPossiblyWithoutSyntaxErrors.forEach { diagnostic ->
            val noqaOffset = diagnostic.getNoqaOffset(document)
            
            val fixes = listOfNotNull(
                diagnostic.makeFixViolationFix(configurations),
                diagnostic.makeDisableRuleCommentFix(configurations, noqaOffset)
            )
            
            val descriptor = when (diagnostic.isForFile) {
                true -> file.createFileProblemDescriptor(diagnostic, fixes)
                else -> file.createProblemDescriptor(diagnostic, fixes)
            }
            
            problemsHolder.registerProblem(descriptor)
        }
    }
    
    private fun PsiFile.createFileProblemDescriptor(
        diagnostic: Diagnostic,
        fixes: List<LocalQuickFix>
    ): ProblemDescriptor {
        val (startElement, endElement) = Pair(this, this)
        val (showTooltip, hintAction, onTheFly) = Triple(true, null, true)
        
        val rangeInElement = TextRange(0, 0)
        val rangeIsAfterEndOfLine = false
        
        return ProblemDescriptorImpl(
            startElement, endElement,
            diagnostic.message,
            fixes.toTypedArray(),
            problemHighlightType,
            rangeIsAfterEndOfLine,
            rangeInElement,
            showTooltip, hintAction, onTheFly
        )
    }
    
    private fun PsiFile.createProblemDescriptor(
        diagnostic: Diagnostic,
        fixes: List<LocalQuickFix>
    ): ProblemDescriptor {
        val document = viewProvider.document
        val range = document.getOffsetRange(diagnostic.oneBasedRange)
        
        val startElement = findElementAt(range.startOffset)!!
        val endElement = when (range.isEmpty) {
            true -> startElement
            else -> findElementAt(range.endOffset - 1)!!
        }
        
        val (showTooltip, hintAction, onTheFly) = Triple(true, null, true)
        val rangeIsAfterEndOfLine = document.rangeIsAfterEndOfLine(range)
        val rangeInElement = range.shiftLeft(startElement.startOffset)
        
        return ProblemDescriptorImpl(
            startElement, endElement,
            diagnostic.message,
            fixes.toTypedArray(),
            problemHighlightType,
            rangeIsAfterEndOfLine,
            rangeInElement,
            showTooltip, hintAction, onTheFly
        )
    }
    
    override fun inspectionFinished(
        manager: InspectionManager,
        globalContext: GlobalInspectionContext,
        problemDescriptionsProcessor: ProblemDescriptionsProcessor
    ) {
        val results = globalContext.getUserData(RESULTS) ?: return
        val diagnosticsWithNoPaths = results[null] ?: return
        
        val logger = manager.project.ruffLogger
        
        logger?.debug("Diagnostics with no path returned during global inspection:")
        logger?.debug(diagnosticsWithNoPaths.stringifyToJSON())
    }
    
}
