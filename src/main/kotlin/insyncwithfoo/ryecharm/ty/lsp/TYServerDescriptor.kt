package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspCompletionCustomizer
import com.intellij.platform.lsp.api.customization.LspCompletionDisabled
import com.intellij.platform.lsp.api.customization.LspCustomization
import com.intellij.platform.lsp.api.customization.LspDiagnosticsCustomizer
import com.intellij.platform.lsp.api.customization.LspDiagnosticsDisabled
import com.intellij.platform.lsp.api.customization.LspInlayHintCustomizer
import com.intellij.platform.lsp.api.customization.LspInlayHintDisabled
import insyncwithfoo.ryecharm.common.logging.tyLogger
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.isSupportedByTY
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ty.WorkspaceConfiguration
import insyncwithfoo.ryecharm.ty.createInitializationOptionsObject
import insyncwithfoo.ryecharm.ty.createWorkspaceConfigurationObject
import org.eclipse.lsp4j.ClientCapabilities
import org.eclipse.lsp4j.ConfigurationItem
import java.nio.file.Path


internal class TYServerDescriptor(project: Project, private val executable: Path) :
    ProjectWideLspServerDescriptor(project, PRESENTABLE_NAME)
{
    
    private val configurations = project.tyConfigurations
    
    override val lspCustomization = object : LspCustomization() {
        
        override val diagnosticsCustomizer: LspDiagnosticsCustomizer
            get() = when (configurations.diagnostics) {
                true -> DiagnosticsSupport()
                else -> LspDiagnosticsDisabled
            }
        
        override val inlayHintCustomizer: LspInlayHintCustomizer
            get() = when (configurations.inlayHints) {
                true -> InlayHintSupport()
                else -> LspInlayHintDisabled
            }
        
        override val completionCustomizer: LspCompletionCustomizer
            get() = when (configurations.completions) {
                true -> CompletionSupport()
                else -> LspCompletionDisabled
            }
        
    }
    
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
    
    override fun getWorkspaceConfiguration(item: ConfigurationItem): WorkspaceConfiguration? {
        val value = when (item.section) {
            "ty" -> project.createWorkspaceConfigurationObject()
            else -> null
        }
        
        return value.also {
            val logger = project.tyLogger
            
            logger?.info("Sending workspace configuration item for ${item.section} @ ${item.scopeUri}:")
            logger?.info("$it")
            logger?.info("")
        }
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
