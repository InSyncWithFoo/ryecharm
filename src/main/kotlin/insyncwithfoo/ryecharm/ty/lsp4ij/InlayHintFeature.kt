package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPInlayHintFeature
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations


@Suppress("UnstableApiUsage")
internal class InlayHintFeature : LSPInlayHintFeature() {
    
    private val configurations by lazy {
        project.tyConfigurations
    }
    
    override fun isEnabled(file: PsiFile) =
        configurations.inlayHints
    
}
