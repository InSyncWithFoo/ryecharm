package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import insyncwithfoo.ryecharm.common.logging.tyLogger
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.isSupportedByTy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import org.eclipse.lsp4j.ClientCapabilities
import org.eclipse.lsp4j.DiagnosticCapabilities
import java.nio.file.Path


internal class TyServerDescriptor(project: Project, private val executable: Path) :
    ProjectWideLspServerDescriptor(project, PRESENTABLE_NAME)
{
    
    private val configurations = project.tyConfigurations
    
    init {
        val logger = project.tyLogger
        
        logger?.info("Starting Ty's language server (native client).")
        logger?.info("")
        logger?.info("Executable: $executable")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: $configurations")
        logger?.info("")
    }
    
    /**
     * @see TextDocumentDiagnosticsPuller
     */
    override val clientCapabilities: ClientCapabilities
        get() = super.clientCapabilities.apply {
            textDocument.apply {
                diagnostic = DiagnosticCapabilities().apply {
                    relatedDocumentSupport = false
                    dynamicRegistration = false
                }
            }
        }
    
    override fun isSupportedFile(file: VirtualFile) =
        file.isSupportedByTy(project)
    
    override fun createInitializationOptions() =
        Object()
    
    override fun createCommandLine() = GeneralCommandLine().apply {
        withWorkingDirectory(project.path)
        withCharset(Charsets.UTF_8)
        
        withExePath(executable.toString())
        addParameter("server")
    }
    
    companion object {
        private val PRESENTABLE_NAME = message("languageServers.ty.presentableName")
    }
    
}
