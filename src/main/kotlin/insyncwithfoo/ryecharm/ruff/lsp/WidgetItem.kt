package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import insyncwithfoo.ryecharm.RuffIcons
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurable
import insyncwithfoo.ryecharm.message


@Suppress("UnstableApiUsage")
internal class WidgetItem(lspServer: LspServer, currentFile: VirtualFile?) :
    LspServerWidgetItem(lspServer, currentFile, RuffIcons.TINY_16_WHITE, RuffConfigurable::class.java) {
    
    private val nameWithVersion: String
        get() = lspServer.initializeResult?.serverInfo?.version
            ?.let { message("languageServer.nameWithVersion", it) }
            ?: lspServer.descriptor.presentableName
    
    override val serverLabel: String
        get() = nameWithVersion + rootPostfix
    
}
