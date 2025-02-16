package insyncwithfoo.ryecharm.redknot.server

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import insyncwithfoo.ryecharm.RyeCharmRegistry
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.isSupportedByRuff


@Suppress("UnstableApiUsage")
internal class RedKnotServerSupportProvider : LspServerSupportProvider {
    
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        WidgetItem(lspServer, currentFile)
    
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {
        if (RyeCharmRegistry.redknot.panels && file.isSupportedByRuff(project)) {
            val executable = project.redKnotExecutable ?: return
            serverStarter.ensureServerStarted(RedKnotServerDescriptor(project, executable))
        }
    }
    
}
