package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutput
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService.Feature
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.sh.formatter.ShExternalFormatter
import com.jetbrains.python.black.BlackFormattingService
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.addOpenPluginIssueTrackerAction
import insyncwithfoo.ryecharm.addSeeOutputActions
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.editorFactory
import insyncwithfoo.ryecharm.errorNotificationGroup
import insyncwithfoo.ryecharm.information
import insyncwithfoo.ryecharm.informationNotificationGroup
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.getOneBasedRange
import insyncwithfoo.ryecharm.runThenNotify
import insyncwithfoo.ryecharm.unableToRunCommand
import java.time.Duration


private val ProcessOutput.isProbablySyntaxErrorFailure: Boolean
    get() = stderrLines.lastOrNull()?.contains("Failed to parse") == true


private fun AsyncFormattingRequest.cannotFormatMultipleRanges() {
    val title = message("notifications.cannotFormatMultipleRanges.title")
    val body = message("notifications.cannotFormatMultipleRanges.body")
    
    onError(title, body)
}


private fun AsyncFormattingRequest.noChange() {
    onTextReady(null)
}


private fun Project.fileContainsSyntaxError(output: ProcessOutput) {
    val title = message("notifications.fileContainsSyntaxError.title")
    val body = message("notifications.fileContainsSyntaxError.body")
    
    informationNotificationGroup.information(title, body).runThenNotify(this) {
        addSeeOutputActions(output)
    }
}


private fun Project.unknownFormattingError(output: ProcessOutput) {
    val title = message("notifications.unknownFormattingError.title")
    val body = message("notifications.unknownFormattingError.body")
    
    informationNotificationGroup.information(title, body).runThenNotify(this) {
        addSeeOutputActions(output)
        addOpenPluginIssueTrackerAction()
    }
}


private fun AsyncFormattingRequest.errorHappened(output: ProcessOutput) {
    noChange()
    
    val project = context.project
    
    when (output.isProbablySyntaxErrorFailure) {
        true -> project.fileContainsSyntaxError(output)
        else -> project.unknownFormattingError(output)
    }
}


private fun String.getOneBasedRange(offsetRange: TextRange): OneBasedRange {
    val document = editorFactory.createDocument(this)
    return document.getOneBasedRange(offsetRange)
}


private fun OSProcessHandler.addProcessTerminatedListener(action: CapturingProcessAdapter.(ProcessEvent) -> Unit) {
    val listener = object : CapturingProcessAdapter() {
        override fun processTerminated(event: ProcessEvent) {
            action(this, event)
        }
    }
    addProcessListener(listener)
}


private class RuffFormattingTask(private val request: AsyncFormattingRequest, command: Command) :
    RuffFormatter.FormattingTask() {
    
    private val handler = command.processHandler
    
    override fun isRunUnderProgress() = true
    
    override fun run() = handler.run {
        addProcessTerminatedListener { event ->
            val configurations = request.context.project.ruffConfigurations
            
            handleOutput(event, snoozeError = configurations.snoozeFormattingTaskError)
        }
        startNotify()
    }
    
    private fun CapturingProcessAdapter.handleOutput(event: ProcessEvent, snoozeError: Boolean) = when {
        event.exitCode == 0 -> request.onTextReady(output.stdout)
        snoozeError -> request.noChange()
        else -> request.errorHappened(output)
    }
    
    override fun cancel(): Boolean {
        handler.destroyProcess()
        return true
    }
    
}


/**
 * Format a document on reformat (`Ctrl` + `Alt` + `L`).
 * 
 * @see ShExternalFormatter
 * @see BlackFormattingService
 */
internal class RuffFormatter : AsyncDocumentFormattingService() {
    
    override fun getName() = "Ruff"
    
    override fun getNotificationGroupId() =
        errorNotificationGroup.displayId
    
    // https://github.com/astral-sh/ruff/issues/8232
    /**
     * Return a set of additional supported formatting method.
     * 
     * This method only returns [Feature.FORMAT_FRAGMENTS],
     * since it is unknown what [Feature.AD_HOC_FORMATTING] means.
     * 
     * [Feature.OPTIMIZE_IMPORTS] must not be specified,
     * as it is currently not possible to
     * both format and optimize in one process.
     */
    override fun getFeatures() = setOf(Feature.FORMAT_FRAGMENTS)
    
    override fun canFormat(file: PsiFile) =
        file.project.ruffConfigurations.run { formatting && formatOnReformat } && file.isSupportedByRuff
    
    /**
     * Return the limit at which the process will be destroyed.
     * 
     * @see AsyncDocumentFormattingService.DEFAULT_TIMEOUT
     */
    override fun getTimeout(): Duration =
        Duration.ofSeconds(10)
    
    /**
     * @see Feature.OPTIMIZE_IMPORTS
     */
    override fun getImportOptimizers(file: PsiFile) =
        setOf(RuffImportOptimizer())
    
    /**
     * Return [RuffFormattingTask], which is responsible
     * for running the command and invoking the callbacks.
     * 
     * Somehow this is triggered with only one range
     * even when multiple are selected in the editor.
     */
    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val ranges = request.formattingRanges
        
        // https://github.com/astral-sh/ruff/issues/12800
        if (ranges.size > 1) {
            request.cannotFormatMultipleRanges()
            return null
        }
        
        val context = request.context
        val project = context.project
        val ruff = project.ruff
        
        if (ruff == null) {
            project.unableToRunCommand()
            return null
        }
        
        val text = request.documentText
        val path = context.virtualFile?.toNioPathOrNull()
        val range = ranges.singleOrNull()?.let { text.getOneBasedRange(it) }
        
        val command = ruff.format(text, path, range, quiet = false)
        
        return RuffFormattingTask(request, command)
    }
    
    /**
     * Expose [AsyncDocumentFormattingService.FormattingTask] so that
     * [RuffFormattingTask] doesn't have to be defined within [RuffFormatter].
     */
    abstract class FormattingTask : AsyncDocumentFormattingService.FormattingTask
    
}
