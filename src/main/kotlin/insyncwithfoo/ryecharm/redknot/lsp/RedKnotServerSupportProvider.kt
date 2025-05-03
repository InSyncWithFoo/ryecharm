package insyncwithfoo.ryecharm.redknot.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.configurations.redknot.RunningMode
import insyncwithfoo.ryecharm.configurations.redknot.redKnotConfigurations
import insyncwithfoo.ryecharm.isSupportedByRedKnot


internal class RedKnotServerSupportProvider : LspServerSupportProvider {
    
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        WidgetItem(lspServer, currentFile)
    
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {
        val configurations = project.redKnotConfigurations
        val runningModeIsLSP = configurations.runningMode == RunningMode.LSP
        
        if (runningModeIsLSP && file.isSupportedByRedKnot(project)) {
            val executable = project.redKnotExecutable ?: return
            serverStarter.ensureServerStarted(RedKnotServerDescriptor(project, executable))
        }
    }
    
}
