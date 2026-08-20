package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.LspIntegrationProvider.LspClientStarter
import insyncwithfoo.ryecharm.canBeLintedByRuff
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable


internal class RuffIntegrationProvider : LspIntegrationProvider {
    
    override fun createWidgetItem(lspClient: LspClient, currentFile: VirtualFile?) =
        WidgetItem(lspClient, currentFile)
    
    override fun fileOpened(project: Project, file: VirtualFile, clientStarter: LspClientStarter) {
        val configurations = project.ruffConfigurations
        val runningModeIsLSP = configurations.runningMode == RunningMode.LSP
        
        if (runningModeIsLSP && file.canBeLintedByRuff(project)) {
            val executable = project.ruffExecutable ?: return
            clientStarter.ensureClientStarted(RuffDescriptor(project, executable))
        }
    }
    
}
