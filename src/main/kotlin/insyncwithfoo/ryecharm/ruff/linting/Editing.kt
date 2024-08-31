package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.openapi.editor.Document
import insyncwithfoo.ryecharm.replaceString
import insyncwithfoo.ryecharm.ruff.OneBasedPinpoint
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.getOffsetRange


private fun SourceLocation.toOneBasedPinpoint() =
    OneBasedPinpoint(row, column)


internal val Ranged.oneBasedRange: OneBasedRange
    get() {
        val start = location.toOneBasedPinpoint()
        val end = endLocation.toOneBasedPinpoint()
        
        return OneBasedRange(start, end)
    }


internal fun Document.performEdit(edit: ExpandedEdit) {
    val range = getOffsetRange(edit.oneBasedRange)
    replaceString(range, edit.content)
}
