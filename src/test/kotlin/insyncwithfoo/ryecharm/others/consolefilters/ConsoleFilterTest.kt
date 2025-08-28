package insyncwithfoo.ryecharm.others.consolefilters

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import insyncwithfoo.ryecharm.PlatformTestCase


private fun String.splitLinesKeepSeparators() =
    this.split("(?<=\n)".toRegex())


/**
 * @see insyncwithfoo.ryecharm.ruff.linting.getInterleavedRanges
 */
private fun String.getInterleavedRanges(ranges: Iterable<IntRange>): List<IntRange> {
    val result = mutableListOf<IntRange>()
    var current = 0
    
    for (range in ranges) {
        if (current < range.first) {
            result.add(current..<range.first)
        }
        
        result.add(range)
        current = range.last + 1
    }
    
    if (current < length) {
        result.add(current..<length)
    }
    
    return result
}

/**
 * @see insyncwithfoo.ryecharm.ruff.linting.RuffFixViolation.performEdits
 */
private fun String.replaceRanges(rangesToReplacements: Map<IntRange, String>): String {
    val interleavedRanges = getInterleavedRanges(rangesToReplacements.keys)
    val result = StringBuilder(this.length)
    
    for (range in interleavedRanges) {
        when (val replacement = rangesToReplacements[range]) {
            null -> result.append(this.substring(range))
            else -> result.append(replacement)
        }
    }
    
    return result.toString()
}


private fun VirtualFile.getRelativePathFromProjectRoot(project: Project) =
    when (val projectPath = project.guessProjectDir()) {
        null -> this.toNioPathOrNull()
        else -> VfsUtilCore.getRelativePath(this, projectPath)?.toNioPathOrNull()
    }


// From:
// https://github.com/intellij-rust/intellij-rust/blob/c6657c02bb/src/test/kotlin/org/rust/RsTestBase.kt
internal abstract class ConsoleFilterTest : PlatformTestCase() {
    
    protected abstract val filter: Filter
    
    /**
     * Apply [filter] to [before], then convert the ranges
     * marked by [OpenFileHyperlinkInfo] items to the format
     * `[${originalText} -> ${targetRelativePath}]`
     * and other [HyperlinkInfo] to `[${originalText}]`.
     */
    protected fun filterTest(before: String, after: String) {
        filterTest(filter, before, after)
    }
    
    private fun filterTest(filter: Filter, before: String, after: String) {
        val withLinksRendered = StringBuilder()
        var accumulativeLength = 0
        
        for (line in before.splitLinesKeepSeparators()) {
            val lineOffset = accumulativeLength
            accumulativeLength += line.length
            
            val result = filter.applyFilter(line, accumulativeLength)
            
            if (result == null) {
                withLinksRendered.append(line)
                continue
            }
            
            val items = result.resultItems.sortedByDescending { it.highlightEndOffset }
            val rangesToReplacements = items.asRangesToReplacements(line, lineOffset)
            
            withLinksRendered.append(line.replaceRanges(rangesToReplacements))
        }
        
        assertEquals(after, withLinksRendered.toString())
    }
    
    private fun List<Filter.ResultItem>.asRangesToReplacements(line: String, lineOffset: Int) = this.associate { item ->
        val range = item.highlightStartOffset..<item.highlightEndOffset
        val rangeWithinLine = (range.first - lineOffset)..(range.last - lineOffset)
        
        var text = line.substring(rangeWithinLine)
        
        (item.hyperlinkInfo as? OpenFileHyperlinkInfo)?.let { link ->
            val target = link.descriptor?.file
            val targetPath = target?.getRelativePathFromProjectRoot(project)
            
            text = "$text -> $targetPath"
        }
        
        rangeWithinLine to "[$text]"
    }
    
}
