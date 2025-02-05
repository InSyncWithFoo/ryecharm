package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import insyncwithfoo.ryecharm.TopPriorityAction
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.replaceContentWith
import insyncwithfoo.ryecharm.ruff.getOffsetRange


/**
 * ```kotlin
 * document.textLength
 * // 10
 * document.getInterleavedRanges(listOf(TextRange(1, 4), TextRange(5, 10)))
 * // listOf(TextRange(0, 1), TextRange(1, 4), TextRange(4, 5), TextRange(5, 10))
 * ```
 */
private fun Document.getInterleavedRanges(ranges: Iterable<TextRange>): List<TextRange> {
    val result = mutableListOf<TextRange>()
    var current = 0
    
    for (range in ranges) {
        if (current < range.startOffset) {
            result.add(TextRange(current, range.startOffset))
        }
        
        result.add(range)
        current = range.endOffset
    }
    
    if (current < textLength) {
        result.add(TextRange(current, textLength))
    }
    
    return result
}


@Suppress("ActionIsNotPreviewFriendly")
internal class RuffFixViolation(private val code: String, private val fix: Fix) : LocalQuickFix, TopPriorityAction {
    
    override fun getFamilyName(): String {
        val (message, applicability) = Pair(fix.message, fix.applicability)
        
        // TODO: Use the same format as the server (?)
        //  > Ruff (A123): Message (applicability)
        return when (message) {
            null -> message("intentions.ruff.fixViolation.familyName.fallback")
            else -> message("intentions.ruff.fixViolation.familyName", code, message, applicability)
        }
    }
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        
        file.edit { document ->
            document.performEdits(fix.edits)
        }
    }
    
    private fun Document.performEdits(edits: List<ExpandedEdit>) {
        val rangesToEdits = edits.associateBy { getOffsetRange(it.oneBasedRange) }
        val interleavedRanges = getInterleavedRanges(rangesToEdits.keys)
        
        val newText = StringBuilder()
        
        for (range in interleavedRanges) {
            when (val edit = rangesToEdits[range]) {
                null -> newText.append(range.subSequence(charsSequence))
                else -> newText.append(edit.content)
            }
        }
        
        replaceContentWith(newText)
    }
    
}
