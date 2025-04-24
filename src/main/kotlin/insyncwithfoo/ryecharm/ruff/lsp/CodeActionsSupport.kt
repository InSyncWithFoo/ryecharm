package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.customization.LspCodeActionsSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.isRuffDisableRuleComment
import insyncwithfoo.ryecharm.ruff.isRuffFixAll
import insyncwithfoo.ryecharm.ruff.isRuffFixViolation
import insyncwithfoo.ryecharm.ruff.isRuffOrganizeImports
import org.eclipse.lsp4j.CodeAction


/**
 * Signify that the client supports code actions.
 * 
 * It is not possible to show applicability in quick fix messages,
 * as applicability is not sent via the LSP.
 */
internal class CodeActionsSupport(project: Project) : LspCodeActionsSupport() {
    
    private val configurations = project.ruffConfigurations
    
    override fun createIntentionAction(lspServer: LspServer, codeAction: CodeAction) = when {
        codeAction.isRuffFixAll && !configurations.fixAll -> null
        codeAction.isRuffOrganizeImports && !configurations.organizeImports -> null
        else -> super.createIntentionAction(lspServer, codeAction)
    }
    
    override fun createQuickFix(lspServer: LspServer, codeAction: CodeAction) = when {
        codeAction.isRuffDisableRuleComment && !configurations.disableRuleComment -> null
        codeAction.isRuffFixViolation && !configurations.fixViolation -> null
        else -> super.createQuickFix(lspServer, codeAction)
    }
    
}
