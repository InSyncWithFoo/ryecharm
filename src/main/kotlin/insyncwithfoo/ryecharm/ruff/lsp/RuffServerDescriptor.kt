package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspCodeActionsCustomizer
import com.intellij.platform.lsp.api.customization.LspCodeActionsDisabled
import com.intellij.platform.lsp.api.customization.LspCustomization
import com.intellij.platform.lsp.api.customization.LspDiagnosticsCustomizer
import com.intellij.platform.lsp.api.customization.LspDiagnosticsDisabled
import com.intellij.platform.lsp.api.customization.LspFormattingCustomizer
import com.intellij.platform.lsp.api.customization.LspFormattingDisabled
import com.intellij.platform.lsp.api.customization.LspHoverCustomizer
import com.intellij.platform.lsp.api.customization.LspHoverDisabled
import insyncwithfoo.ryecharm.common.logging.ruffLogger
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isExpectedByRuffServer
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.createInitializationOptionsObject
import org.eclipse.lsp4j.ClientCapabilities
import java.nio.file.Path


internal class RuffServerDescriptor(project: Project, private val executable: Path) :
    ProjectWideLspServerDescriptor(project, PRESENTABLE_NAME)
{
    
    private val configurations = project.ruffConfigurations
    
    override val lspCustomization = object : LspCustomization() {
        
        override val codeActionsCustomizer: LspCodeActionsCustomizer
            get() = when (configurations.quickFixes) {
                true -> CodeActionsSupport(project)
                else -> LspCodeActionsDisabled
            }
        
        override val diagnosticsCustomizer: LspDiagnosticsCustomizer
            get() = when (configurations.linting) {
                true -> DiagnosticsSupport(project)
                else -> LspDiagnosticsDisabled
            }
        
        override val formattingCustomizer: LspFormattingCustomizer
            get() = when (configurations.formatting) {
                true -> FormattingSupport(project)
                else -> LspFormattingDisabled
            }
        
        override val hoverCustomizer: LspHoverCustomizer
            get() = when (configurations.documentationPopups) {
                true -> super.hoverCustomizer
                else -> LspHoverDisabled
            }
        
    }
    
    override val clientCapabilities: ClientCapabilities
        get() = super.clientCapabilities.apply {
            textDocument.apply {
                diagnostic = diagnostic.takeIf { configurations.letNativeClientPullDiagnostics }
            }
        }
    
    init {
        val logger = project.ruffLogger
        
        logger?.info("Starting Ruff's language server (native client).")
        logger?.info("")
        logger?.info("Executable: $executable")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: $configurations")
        logger?.info("")
    }
    
    /**
     * Diagnostics for files told to be unsupported
     * are ignored by the client.
     */
    override fun isSupportedFile(file: VirtualFile) =
        file.isExpectedByRuffServer
    
    override fun createInitializationOptions() =
        project.createInitializationOptionsObject().also {
            val logger = project.ruffLogger
            
            logger?.info("Sending initializationOptions:")
            logger?.info("$it")
            logger?.info("")
        }
    
    override fun createCommandLine() = GeneralCommandLine().apply {
        withWorkingDirectory(project.path)
        withCharset(Charsets.UTF_8)
        
        withExePath(executable.toString())
        addParameter("server")
    }
    
    companion object {
        private val PRESENTABLE_NAME = message("languageServers.ruff.presentableName")
    }
    
}
