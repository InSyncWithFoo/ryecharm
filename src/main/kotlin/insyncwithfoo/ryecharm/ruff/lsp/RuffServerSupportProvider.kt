package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import insyncwithfoo.ryecharm.canBeLintedByRuff
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable


@Suppress("UnstableApiUsage")
internal class RuffServerSupportProvider : LspServerSupportProvider {
    
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        WidgetItem(lspServer, currentFile)
    
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {
        val configurations = project.ruffConfigurations
        val runningModeIsLSP = configurations.runningMode == RunningMode.LSP
        
        if (runningModeIsLSP && file.canBeLintedByRuff(project)) {
            val executable = project.ruffExecutable ?: return
            serverStarter.ensureServerStarted(RuffServerDescriptor(project, executable))
        }
    }
    
}
