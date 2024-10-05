package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPHoverFeature
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.RuffServerDescriptor.lspHoverSupport
 */
@Suppress("UnstableApiUsage")
internal class HoverFeature : LSPHoverFeature() {
    
    private val configurations = project.ruffConfigurations
    
    override fun isEnabled(file: PsiFile) =
        configurations.run { documentationPopups && documentationPopupsForNoqaComments }
    
}
