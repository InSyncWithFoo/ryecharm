package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import insyncwithfoo.ryecharm.application
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
    
    override val lspGoToDefinitionSupport = false
    override val lspCompletionSupport = null
    override val lspCommandsSupport = null
    
    override val lspHoverSupport = configurations.documentationPopups
    override val lspDiagnosticsSupport = DiagnosticsSupport(project).takeIf { configurations.linting }
    override val lspCodeActionsSupport = CodeActionsSupport(project).takeIf { configurations.quickFixes }
    override val lspFormattingSupport = FormattingSupport(project).takeIf { configurations.formatting }
    
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
    
    override fun getFilePath(file: VirtualFile) =
        when (application.isUnitTestMode) {
            true -> Path.of(project.basePath!!, file.path.removePrefix("/")).toString()
            else -> super.getFilePath(file)
        }
    
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
