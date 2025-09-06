package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.ZeroBasedIndex
import insyncwithfoo.ryecharm.ruff.getOffsetRange
import insyncwithfoo.ryecharm.ruff.toZeroBased
import insyncwithfoo.ryecharm.ruff.DiagnosticID
import insyncwithfoo.ryecharm.ruff.isLikelySyntaxError


/**
 * @see insyncwithfoo.ryecharm.ruff.isForSyntaxError
 */
internal val Diagnostic.isForSyntaxError: Boolean
    get() = id.isLikelySyntaxError


/**
 * @see insyncwithfoo.ryecharm.ruff.diagnosticIsForFile
 */
internal val Diagnostic.isForFile: Boolean
    get() = oneBasedRange == OneBasedRange.FILE_LEVEL


private val Diagnostic.isUnsuppressable: Boolean
    get() = (id as? DiagnosticID.Lint)?.value in listOf(
        "PGH004"  // blanket-noqa
    )


internal fun Diagnostic.makeFixViolationFix(configurations: RuffConfigurations) =
    when {
        !configurations.quickFixes || !configurations.fixViolation -> null
        fix == null || id !is DiagnosticID.Lint -> null
        else -> RuffFixViolation(id.value, fix)
    }


internal fun Diagnostic.makeFixSimilarViolationsFixes(configurations: RuffConfigurations) =
    when {
        !configurations.quickFixes || !configurations.fixSimilarViolations -> null
        fix == null || id !is DiagnosticID.Lint -> null
        else -> Pair(
            RuffFixSimilarViolations(id.value, unsafe = false),
            RuffFixSimilarViolations(id.value, unsafe = true)
        )
    }


internal fun Diagnostic.makeDisableRuleCommentFix(configurations: RuffConfigurations, offset: ZeroBasedIndex) =
    when {
        !configurations.quickFixes || !configurations.disableRuleComment -> null
        id !is DiagnosticID.Lint || this.isUnsuppressable -> null
        else -> RuffDisableRuleComment(id.value, offset)
    }


internal fun Diagnostic.getNoqaOffset(document: Document) =
    when (noqaRow) {
        null -> document.getOffsetRange(oneBasedRange).startOffset
        else -> document.getLineStartOffset(noqaRow.toZeroBased())
    }


internal fun Document.rangeIsAfterEndOfLine(range: TextRange): Boolean {
    val rangeIsAtEOF = range.endOffset == textLength
    val rangeIsAtLineBreak = charsSequence.getOrNull(range.startOffset) == '\n'
    
    return rangeIsAtEOF || rangeIsAtLineBreak
}
