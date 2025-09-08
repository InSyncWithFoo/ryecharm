package insyncwithfoo.ryecharm.others.consolefilters

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.resolveFromRootOrRelative
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.ruff.toZeroBased
import insyncwithfoo.ryecharm.sourceRoots


internal class RuffAndTYPathLinker(private val project: Project) : Filter, DumbAware {
    
    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        val lineBreakTrimmed = line.trimEnd()
        val (prefix, path, oneBasedLineIndex, oneBasedColumnIndex) =
            PATTERN.matchEntire(lineBreakTrimmed)?.destructured ?: return null
        
        val virtualFile = project.resolve(path.replace("\\", "/")) ?: return null
        
        val lineOffset = entireLength - line.length
        val (linkStart, linkEnd) = Pair(lineOffset + prefix.length, lineOffset + lineBreakTrimmed.length)
        val linkInfo = OpenFileHyperlinkInfo(
            project,
            virtualFile,
            oneBasedLineIndex.toInt().toZeroBased(),
            oneBasedColumnIndex.toInt().toZeroBased()
        )
        
        val item = Filter.ResultItem(linkStart, linkEnd, linkInfo)
        
        return Filter.Result(listOf(item))
    }
    
    private fun Project.resolve(path: String): VirtualFile? {
        val configurations = mainConfigurations
        val possibleBases = mutableSetOf<VirtualFile>()
        
        guessProjectDir()?.let { possibleBases += it }
        
        if (configurations.resolveRuffTYPathsAgainstSourceRoots) {
            possibleBases += sourceRoots
        }
        
        return possibleBases.firstNotNullOfOrNull {
            it.resolveFromRootOrRelative(path)
        }
    }
    
    companion object {
        private val PATTERN = """( *(?:-->|:::) )(.+):(\d+):(\d+)""".toRegex()
    }
    
}
