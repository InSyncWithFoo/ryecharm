package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import insyncwithfoo.ryecharm.configurations.ty.TYConfigurable
import insyncwithfoo.ryecharm.message


internal class WidgetItem(lspServer: LspServer, currentFile: VirtualFile?) :
    LspServerWidgetItem(lspServer, currentFile, AllIcons.Json.Object, TYConfigurable::class.java)
{
    
    private val nameWithVersion: String
        get() = lspServer.initializeResult?.serverInfo?.version
            ?.let { message("languageServers.ty.nameWithVersion", it) }
            ?: lspServer.descriptor.presentableName
    
    override val serverLabel: String
        get() = nameWithVersion + rootPostfix
    
}
