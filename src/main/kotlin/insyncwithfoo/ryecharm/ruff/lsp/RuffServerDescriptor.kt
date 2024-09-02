package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.createInitializationOptionsObject
import java.nio.file.Path


@Suppress("UnstableApiUsage")
internal class RuffServerDescriptor(project: Project, private val executable: Path) :
    ProjectWideLspServerDescriptor(project, PRESENTABLE_NAME) {
    
    private val configurations = project.ruffConfigurations
    
    override val lspGoToDefinitionSupport = false
    override val lspCompletionSupport = null
    override val lspCommandsSupport = null
    
    override val lspHoverSupport = configurations.run { documentationPopups && documentationPopupsForNoqaComments }
    override val lspDiagnosticsSupport = DiagnosticsSupport(project).takeIf { configurations.linting }
    override val lspCodeActionsSupport = CodeActionsSupport(project).takeIf { configurations.quickFixes }
    override val lspFormattingSupport = FormattingSupport(project).takeIf { configurations.formatting }
    
    init {
        LOGGER.info(configurations.toString())
    }
    
    /**
     * Diagnostics for files told to be unsupported
     * are ignored by the client.
     */
    override fun isSupportedFile(file: VirtualFile) =
        file.isSupportedByRuff(project)
    
    override fun createInitializationOptions() =
        project.createInitializationOptionsObject()
    
    override fun createCommandLine() = GeneralCommandLine().apply {
        withWorkingDirectory(project.path)
        withCharset(Charsets.UTF_8)
        
        withExePath(executable.toString())
        addParameter("server")
    }
    
    companion object {
        private val LOGGER by ::LOG
        private val PRESENTABLE_NAME = message("languageServer.presentableName")
    }
    
}
