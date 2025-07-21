package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import insyncwithfoo.ryecharm.application
import insyncwithfoo.ryecharm.common.logging.tyLogger
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.isSupportedByTY
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ty.createInitializationOptionsObject
import org.eclipse.lsp4j.ClientCapabilities
import java.nio.file.Path
import kotlin.io.path.div


internal class TYServerDescriptor(project: Project, private val executable: Path) :
    ProjectWideLspServerDescriptor(project, PRESENTABLE_NAME)
{
    
    private val configurations = project.tyConfigurations
    
    override val clientCapabilities: ClientCapabilities
        get() = super.clientCapabilities.apply {
            textDocument.apply {
                diagnostic = null
            }
        }
    
    init {
        val logger = project.tyLogger
        
        logger?.info("Starting ty's language server (native client).")
        logger?.info("")
        logger?.info("Executable: $executable")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: $configurations")
        logger?.info("")
    }
    
    override fun isSupportedFile(file: VirtualFile) =
        file.isSupportedByTY(project)
    
    override fun createInitializationOptions() =
        project.createInitializationOptionsObject().also {
            val logger = project.tyLogger
            
            logger?.info("Sending initializationOptions:")
            logger?.info("$it")
            logger?.info("")
        }
    
    override fun getFilePath(file: VirtualFile) =
        when (application.isUnitTestMode) {
            true -> (Path.of(project.basePath!!) / file.path).toString()
            else -> super.getFilePath(file)
        }
    
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
