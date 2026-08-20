package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.LspIntegrationProvider.LspClientStarter
import insyncwithfoo.ryecharm.configurations.ty.RunningMode
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.isSupportedByTY


internal class TYIntegrationProvider : LspIntegrationProvider {
    
    override fun createWidgetItem(lspClient: LspClient, currentFile: VirtualFile?) =
        WidgetItem(lspClient, currentFile)
    
    override fun fileOpened(project: Project, file: VirtualFile, clientStarter: LspClientStarter) {
        val configurations = project.tyConfigurations
        val runningModeIsLSP = configurations.runningMode == RunningMode.LSP
        
        if (runningModeIsLSP && file.isSupportedByTY(project)) {
            val executable = project.tyExecutable ?: return
            clientStarter.ensureClientStarted(TYDescriptor(project, executable))
        }
    }
    
}
