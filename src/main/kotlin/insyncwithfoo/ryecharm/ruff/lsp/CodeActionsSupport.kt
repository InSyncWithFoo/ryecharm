package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.customization.LspCodeActionsSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import org.eclipse.lsp4j.CodeAction


private val CodeAction.isRuffFixAll: Boolean
    get() = kind == "source.fixAll.ruff"


private val CodeAction.isRuffOrganizeImports: Boolean
    get() = kind == "source.organizeImports.ruff"


/**
 * @see isRuffDisableRuleComment
 */
internal val CodeAction.isRuffFixViolation: Boolean
    get() = !this.isRuffDisableRuleComment


// FIXME: Better detection
/**
 * Whether this [CodeAction] is a suppress fix.
 * 
 * The [title][CodeAction.title] is the only discriminant property,
 * save for the actual [edit][CodeAction.edit] content.
 * Such a title looks like:
 * 
 * > Ruff (A123): Disable for this line
 * 
 * As this specific message is not guaranteed in any way,
 * this will break when Ruff adds support for i18n.
 */
internal val CodeAction.isRuffDisableRuleComment: Boolean
    get() = title.endsWith("Disable for this line")


/**
 * Signify that the client supports code actions.
 * 
 * It is not possible to show applicability in quick fix messages,
 * as applicability is not sent via the LSP.
 */
@Suppress("UnstableApiUsage")
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
