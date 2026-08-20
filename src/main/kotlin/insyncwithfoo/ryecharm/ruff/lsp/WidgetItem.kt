package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.lsWidget.LspClientWidgetItem
import insyncwithfoo.ryecharm.RuffIcons
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurable
import insyncwithfoo.ryecharm.message


internal class WidgetItem(lspClient: LspClient, currentFile: VirtualFile?) :
    LspClientWidgetItem(lspClient, currentFile, RuffIcons.TINY_16_WHITE, RuffConfigurable::class.java)
{
    
    private val nameWithVersion: String
        get() = lspClient.initializeResult?.serverInfo?.version
            ?.let { message("languageServers.ruff.nameWithVersion", it) }
            ?: lspClient.descriptor.presentableName
    
    override val itemLabel: String
        get() = nameWithVersion + rootPostfix
    
}
