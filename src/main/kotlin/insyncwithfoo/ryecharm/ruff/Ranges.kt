package insyncwithfoo.ryecharm.ruff

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range


private typealias Index = Int
private typealias RowIndex = Index
private typealias ColumnIndex = Index
internal typealias ZeroBasedIndex = Index
internal typealias OneBasedIndex = Index

private typealias Pinpoint<RowIndex, ColumnIndex> = Pair<RowIndex, ColumnIndex>
private typealias ZeroBasedPinpoint = Pinpoint<ZeroBasedIndex, ZeroBasedIndex>
internal typealias OneBasedPinpoint = Pinpoint<OneBasedIndex, OneBasedIndex>


private val Pinpoint<RowIndex, ColumnIndex>.row: RowIndex
    get() = first


private val Pinpoint<RowIndex, ColumnIndex>.column: ColumnIndex
    get() = second


private fun ZeroBasedPinpoint.toOneBased() =
    OneBasedPinpoint(row + 1, column + 1)


private fun OneBasedPinpoint.toZeroBased() =
    ZeroBasedPinpoint(row - 1, column - 1)


private fun ZeroBasedIndex.toOneBased() =
    this + 1


internal fun OneBasedIndex.toZeroBased() =
    this - 1


/**
 * A Ruff inclusive range.
 */
internal data class OneBasedRange(val start: OneBasedPinpoint, val end: OneBasedPinpoint) {
    
    /**
     * The format expected by `format`'s `--range`.
     */
    override fun toString(): String {
        val (startLine, startColumn) = start
        val (endLine, endColumn) = end
        
        return "${startLine}:${startColumn}-${endLine}:${endColumn}"
    }
    
    companion object {
        /**
         * File-level diagnostics's ranges are (almost) always `0..0`.
         */
        val FILE_LEVEL: OneBasedRange
            get() = OneBasedRange(1 to 1, 1 to 1)
    }
    
}


private fun Document.getZeroBasedPinpoint(offset: ZeroBasedIndex): ZeroBasedPinpoint {
    val line = getLineNumber(offset)
    val column = offset - getLineStartOffset(line)
    
    return ZeroBasedPinpoint(line, column)
}


internal fun Document.getOneBasedRange(offsetRange: TextRange): OneBasedRange {
    val (startOffset, endOffset) =
        Pair(offsetRange.startOffset, offsetRange.endOffset)
    
    val startPinpoint = getZeroBasedPinpoint(startOffset)
    val endPinpoint = getZeroBasedPinpoint(endOffset)
    
    return OneBasedRange(startPinpoint.toOneBased(), endPinpoint.toOneBased())
}


private fun Document.pinpointIsAtEOF(pinpoint: ZeroBasedPinpoint) =
    lineCount == pinpoint.row - 1


private fun Document.getPinpointOffset(pinpoint: ZeroBasedPinpoint) =
    getLineStartOffset(pinpoint.row) + pinpoint.column


internal fun Document.getOffsetRange(oneBasedRange: OneBasedRange): TextRange {
    val (startPinpoint, endPinpoint) = Pair(oneBasedRange.start.toZeroBased(), oneBasedRange.end.toZeroBased())
    
    val startOffset = getPinpointOffset(startPinpoint)
    
    val endOffset = when {
        pinpointIsAtEOF(endPinpoint) -> textLength
        else -> getPinpointOffset(endPinpoint)
    }
    
    return TextRange(startOffset, endOffset)
}


private fun Position.toOneBasedPinpoint() =
    OneBasedPinpoint(line.toOneBased(), character.toOneBased())


internal fun Document.getOffsetRange(lspRange: Range): TextRange {
    val start = lspRange.start.toOneBasedPinpoint()
    val end = lspRange.end.toOneBasedPinpoint()
    
    return getOffsetRange(OneBasedRange(start, end))
}


internal operator fun TextRange.plus(offset: Int) =
    TextRange(startOffset + offset, endOffset + offset)


internal operator fun TextRange.minus(offset: Int) =
    TextRange(startOffset - offset, endOffset - offset)


internal fun IntRange.asTextRange() =
    TextRange(first, last + 1)
