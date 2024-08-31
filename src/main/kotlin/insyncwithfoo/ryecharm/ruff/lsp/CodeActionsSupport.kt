package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.platform.lsp.api.customization.LspCodeActionsSupport


/**
 * Signify that the client supports code actions.
 * 
 * It is not possible to show applicability in quick fix messages,
 * as applicability is not sent via the LSP.
 */
@Suppress("UnstableApiUsage")
internal class CodeActionsSupport : LspCodeActionsSupport()
