package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.customization.LspFormattingSupport
import insyncwithfoo.ryecharm.isSupportedByRuff


@Suppress("UnstableApiUsage")
internal class FormattingSupport(private val project: Project) : LspFormattingSupport() {
    
    override fun shouldFormatThisFileExclusivelyByServer(
        file: VirtualFile,
        ideCanFormatThisFileItself: Boolean,
        serverExplicitlyWantsToFormatThisFile: Boolean
    ) =
        file.isSupportedByRuff(project)
    
}
