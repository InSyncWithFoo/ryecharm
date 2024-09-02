package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.customization.LspFormattingSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff


@Suppress("UnstableApiUsage")
internal class FormattingSupport(private val project: Project) : LspFormattingSupport() {
    
    private val configurations = project.ruffConfigurations
    
    override fun shouldFormatThisFileExclusivelyByServer(
        file: VirtualFile,
        ideCanFormatThisFileItself: Boolean,
        serverExplicitlyWantsToFormatThisFile: Boolean
    ) =
        configurations.formatOnReformat && file.isSupportedByRuff(project)
    
}
