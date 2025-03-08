package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.jetbrains.python.highlighting.PyHighlighter
import com.jetbrains.python.psi.LanguageLevel
import insyncwithfoo.ryecharm.message


/**
 * Determine whether the `script` live template should be suggested.
 * 
 * The cursor is considered to be "in context" when
 * all of the following are true:
 * 
 * * The first character of the current line is not a space.
 * * The file has yet to have a script metadata block.
 */
internal class ScriptMetadataTemplateContext :
    TemplateContextType(message("templates.scriptMetadata.presentableName")) {
    
    override fun createHighlighter() =
        PyHighlighter(LanguageLevel.getLatest())
    
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val viewProvider = templateActionContext.file.viewProvider
        
        if (viewProvider.virtualFile.extension != "py") {
            return false
        }
        
        val document = viewProvider.document ?: return false
        val offset = templateActionContext.startOffset
        val text = document.charsSequence
        
        val line = document.getLineNumber(offset)
        val lineStart = document.getLineStartOffset(line)
        
        if (text.getOrNull(lineStart)?.isWhitespace() == true) {
            return false
        }
        
        return !text.contains(scriptBlock)
    }
    
}
