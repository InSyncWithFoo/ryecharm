package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPCodeActionFeature
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.isRuffDisableRuleComment
import insyncwithfoo.ryecharm.ruff.isRuffFixAll
import insyncwithfoo.ryecharm.ruff.isRuffFixViolation
import insyncwithfoo.ryecharm.ruff.isRuffOrganizeImports
import org.eclipse.lsp4j.CodeAction


/**
 * @see insyncwithfoo.ryecharm.ruff.lsp.CodeActionsSupport
 */
@Suppress("UnstableApiUsage")
internal class CodeActionFeature : LSPCodeActionFeature() {
    
    private val configurations by lazy {
        project.ruffConfigurations
    }
    
    override fun isEnabled(file: PsiFile) =
        configurations.quickFixes
    
    override fun getText(codeAction: CodeAction) = when {
        codeAction.isRuffFixAll && !configurations.fixAll -> null
        codeAction.isRuffOrganizeImports && !configurations.organizeImports -> null
        codeAction.isRuffDisableRuleComment && !configurations.disableRuleComment -> null
        codeAction.isRuffFixViolation && !configurations.fixViolation -> null
        else -> super.getText(codeAction)
    }
    
}
