package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.lsWidget.LspClientWidgetItem
import insyncwithfoo.ryecharm.TYIcons
import insyncwithfoo.ryecharm.configurations.ty.TYConfigurable
import insyncwithfoo.ryecharm.message


internal class WidgetItem(lspClient: LspClient, currentFile: VirtualFile?) :
    LspClientWidgetItem(lspClient, currentFile, TYIcons.TINY_16_WHITE, TYConfigurable::class.java)
{
    
    private val nameWithVersion: String
        get() = lspClient.initializeResult?.serverInfo?.version
            ?.let { message("languageServers.ty.nameWithVersion", it) }
            ?: lspClient.descriptor.presentableName
    
    override val itemLabel: String
        get() = nameWithVersion + rootPostfix
    
}
