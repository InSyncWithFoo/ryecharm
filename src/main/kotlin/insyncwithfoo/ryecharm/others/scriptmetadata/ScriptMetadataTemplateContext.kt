package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.jetbrains.python.highlighting.PyHighlighter
import com.jetbrains.python.psi.LanguageLevel
import insyncwithfoo.ryecharm.message


internal class ScriptMetadataTemplateContext :
    TemplateContextType(message("templates.scriptMetadata.presentableName")) {
    
    override fun createHighlighter() =
        PyHighlighter(LanguageLevel.getLatest())
    
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val document = templateActionContext.file.viewProvider.document ?: return false
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
