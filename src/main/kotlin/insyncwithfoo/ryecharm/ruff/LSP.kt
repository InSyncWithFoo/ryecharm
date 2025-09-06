package insyncwithfoo.ryecharm.ruff

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.util.TextRange
import org.eclipse.lsp4j.CodeAction
import org.eclipse.lsp4j.Diagnostic


internal val Diagnostic.id: DiagnosticID
    get() = DiagnosticID.from(code?.get() as String?)


/**
 * @see insyncwithfoo.ryecharm.ruff.linting.isForSyntaxError
 */
internal val Diagnostic.isForSyntaxError: Boolean
    get() = code == null


/**
 * @see insyncwithfoo.ryecharm.ruff.linting.isForFile
 */
internal val TextRange.diagnosticIsForFile: Boolean
    get() = startOffset == 0 && endOffset == 0


internal val CodeAction.isRuffFixAll: Boolean
    get() = kind == "source.fixAll.ruff"


internal val CodeAction.isRuffOrganizeImports: Boolean
    get() = kind == "source.organizeImports.ruff"


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


internal val IntentionAction.isRuffFixViolation: Boolean
    get() = !this.isRuffDisableRuleComment


internal val IntentionAction.isRuffDisableRuleComment: Boolean
    get() = text.endsWith("Disable for this line")
