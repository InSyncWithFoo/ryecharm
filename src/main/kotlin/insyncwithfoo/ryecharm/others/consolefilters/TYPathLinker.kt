package insyncwithfoo.ryecharm.others.consolefilters

import com.intellij.execution.filters.FileHyperlinkInfoBase
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import insyncwithfoo.ryecharm.localFileSystem
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.toZeroBased


internal class TYPathLinker(private val project: Project) : Filter, DumbAware {
    
    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        val lineBreakTrimmed = line.trimEnd()
        val (prefix, path, oneBasedLineIndex, oneBasedColumnIndex) =
            """( +--> )(.+):(\d+):(\d+)""".toRegex().matchEntire(lineBreakTrimmed)?.destructured ?: return null
        
        val absolutePath = project.path?.resolve(path) ?: return null
        val virtualFile = localFileSystem.findFileByNioFile(absolutePath) ?: return null
        
        val lineOffset = entireLength - line.length
        val (linkStart, linkEnd) = Pair(lineOffset + prefix.length, lineOffset + lineBreakTrimmed.length)
        val linkInfo = FileLinkInfo(
            virtualFile,
            project,
            oneBasedLineIndex.toInt().toZeroBased(),
            oneBasedColumnIndex.toInt().toZeroBased()
        )
        
        val item = Filter.ResultItem(linkStart, linkEnd, linkInfo)
        
        return Filter.Result(listOf(item))
    }
    
    private class FileLinkInfo(
        override val virtualFile: VirtualFile,
        project: Project,
        line: Int,
        column: Int
    ) : FileHyperlinkInfoBase(project, line, column, myUseBrowser = true)
    
}
