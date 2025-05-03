package insyncwithfoo.ryecharm.redknot.lsp

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import insyncwithfoo.ryecharm.configurations.ty.RedKnotConfigurable
import insyncwithfoo.ryecharm.message


internal class WidgetItem(lspServer: LspServer, currentFile: VirtualFile?) :
    LspServerWidgetItem(lspServer, currentFile, AllIcons.Json.Object, RedKnotConfigurable::class.java)
{
    
    private val nameWithVersion: String
        get() = lspServer.initializeResult?.serverInfo?.version
            ?.let { message("languageServers.redknot.nameWithVersion", it) }
            ?: lspServer.descriptor.presentableName
    
    override val serverLabel: String
        get() = nameWithVersion + rootPostfix
    
}
