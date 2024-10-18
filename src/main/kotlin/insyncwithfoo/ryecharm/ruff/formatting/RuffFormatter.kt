package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService.Feature
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.sh.formatter.ShExternalFormatter
import com.jetbrains.python.black.BlackFormattingService
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.editorFactory
import insyncwithfoo.ryecharm.errorNotificationGroup
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.getOneBasedRange
import java.time.Duration


private fun OSProcessHandler.addProcessTerminatedListener(action: CapturingProcessAdapter.(ProcessEvent) -> Unit) {
    val listener = object : CapturingProcessAdapter() {
        override fun processTerminated(event: ProcessEvent) {
            action(this, event)
        }
    }
    addProcessListener(listener)
}


/**
 * Format a document on reformat (`Ctrl` + `L`).
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
     * This method should ideally respect [RuffTimeouts.FORMAT],
     * but it receives zero context and thus
     * cannot retrieve the necessary information.
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
        val ruff = project.ruff ?: return null
        
        val text = request.documentText
        val path = context.virtualFile?.toNioPathOrNull()
        val range = text.getOneBasedRange(ranges.single())
        
        val command = ruff.format(text, path, range)
        
        return try {
            RuffFormattingTask(request, command)
        } catch (error: ExecutionException) {
            LOGGER.error(error)
            request.onError(message("notifications.error.title"), error.message!!)
            return null
        }
    }
    
    private fun AsyncFormattingRequest.cannotFormatMultipleRanges() {
        onError(
            message("notifications.cannotFormatMultipleRanges.title"),
            message("notifications.cannotFormatMultipleRanges.body")
        )
    }
    
    private fun String.getOneBasedRange(offsetRange: TextRange): OneBasedRange {
        val document = editorFactory.createDocument(this)
        return document.getOneBasedRange(offsetRange)
    }
    
    private class RuffFormattingTask(private val request: AsyncFormattingRequest, command: Command) : FormattingTask {
        
        private val handler = command.processHandler
        
        override fun isRunUnderProgress() = true
        
        override fun run() = handler.run {
            addProcessTerminatedListener { event ->
                val configurations = request.context.project.ruffConfigurations
                
                when {
                    event.exitCode == 0 -> request.onTextReady(output.stdout)
                    configurations.snoozeFormattingTaskError -> request.onTextReady(null)
                    else -> request.onError(message("notifications.error.title"), output.stderr)
                }
            }
            startNotify()
        }
        
        override fun cancel(): Boolean {
            handler.destroyProcess()
            return true
        }
        
    }
    
    companion object {
        private val LOGGER = Logger.getInstance(RuffFormatter::class.java)
    }
    
}
