package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPFormattingFeature
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.FormattingSupport
 */
@Suppress("UnstableApiUsage")
internal class FormattingFeature : LSPFormattingFeature() {
    
    private val configurations by lazy {
        project.ruffConfigurations
    }
    
    override fun isEnabled(file: PsiFile) =
        configurations.formatting
    
    override fun isSupported(file: PsiFile) =
        configurations.formatOnReformat && file.isSupportedByRuff
    
}
