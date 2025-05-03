package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.getServers
import insyncwithfoo.ryecharm.isSupportedByTy
import insyncwithfoo.ryecharm.lspServerManager
import insyncwithfoo.ryecharm.ruff.codeAsString
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import insyncwithfoo.ryecharm.ruff.linting.rangeIsAfterEndOfLine
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.DocumentDiagnosticParams
import org.eclipse.lsp4j.TextDocumentIdentifier


internal data class InitialInfo(
    val project: Project,
    val file: VirtualFile
)


internal data class AnnotationResult(
    val diagnostics: List<Diagnostic>
)


// TODO: Remove this
// Upstream issues:
// * https://youtrack.jetbrains.com/issue/IJPL-172853
// * https://github.com/astral-sh/ruff/issues/16743
/**
 * A semi-functional implementation of `textDocument/diagnostics`
 * for Red Knot, as it does not support `/pushDiagnostics`.
 */
internal class TextDocumentDiagnosticsPuller : ExternalAnnotator<InitialInfo, AnnotationResult>(), DumbAware {
    
    private val Project.tyServer: LspServer?
        get() = lspServerManager.getServers<TyServerSupportProvider>().firstOrNull()
    
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): InitialInfo? {
        val project = file.project
        val virtualFile = file.virtualFile ?: return null
        
        if (!file.isSupportedByTy || !virtualFile.isInLocalFileSystem) {
            return null
        }
        
        if (project.tyServer == null) {
            return null
        }
        
        return InitialInfo(project, virtualFile)
    }
    
    override fun doAnnotate(collectedInfo: InitialInfo?): AnnotationResult? {
        val (project, file) = collectedInfo ?: return null
        
        val server = project.tyServer ?: return null
        
        val response = server.sendRequestSync {
            val uri = server.descriptor.getFileUri(file)
            val identifier = TextDocumentIdentifier(uri)
            val params = DocumentDiagnosticParams(identifier)
            
            it.textDocumentService.diagnostic(params)
        }
        
        val report = response?.relatedFullDocumentDiagnosticReport ?: return null
        val diagnostics = report.items ?: return null
        
        return AnnotationResult(diagnostics)
    }
    
    override fun apply(file: PsiFile, annotationResult: AnnotationResult?, holder: AnnotationHolder) {
        val (diagnostics) = annotationResult ?: return
        val document = file.viewProvider.document ?: return
        
        diagnostics.forEach { diagnostic ->
            val message = diagnostic.message
            val highlightSeverity = diagnostic.severity.toHighlightSeverity()
            val builder = holder.newAnnotation(highlightSeverity, message)
            
            builder.needsUpdateOnTyping()
            
            val tooltip = when (val code = diagnostic.codeAsString) {
                null -> message
                else -> "[$code] $message"
            }
            builder.tooltip(tooltip)
            
            val range = document.getOffsetRange(diagnostic.range)
            builder.range(range)
            
            if (document.rangeIsAfterEndOfLine(range)) {
                builder.afterEndOfLine()
            }
            
            builder.create()
        }
    }
    
    private fun DiagnosticSeverity.toHighlightSeverity() = when (this) {
        DiagnosticSeverity.Error -> HighlightSeverity.ERROR
        DiagnosticSeverity.Warning -> HighlightSeverity.WARNING
        DiagnosticSeverity.Information -> HighlightSeverity.WEAK_WARNING
        DiagnosticSeverity.Hint -> HighlightSeverity.INFORMATION
    }
    
}
