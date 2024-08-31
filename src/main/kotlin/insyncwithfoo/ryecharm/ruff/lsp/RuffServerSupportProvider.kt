package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.isSupportedByRuff


@Suppress("UnstableApiUsage")
internal class RuffServerSupportProvider : LspServerSupportProvider {
    
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        WidgetItem(lspServer, currentFile)
    
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter
    ) {
        val configurations = project.ruffConfigurations
        val runningModeIsLSP = configurations.runningMode == RunningMode.LSP
        
        if (runningModeIsLSP && file.isSupportedByRuff(project)) {
            val executable = project.ruffExecutable ?: return
            serverStarter.ensureServerStarted(RuffServerDescriptor(project, executable))
        }
    }
    
}
