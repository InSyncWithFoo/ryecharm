package insyncwithfoo.ryecharm.ruff.tooltips

import com.intellij.codeInsight.highlighting.TooltipLinkHandler
import com.intellij.openapi.editor.Editor
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationByFullCode
import insyncwithfoo.ryecharm.ruff.documentation.isRuleSelector
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.message
import kotlinx.coroutines.runBlocking


private const val IGNORE_THIS_LINK = true
private const val PROCEED_TO_CALL_GET_DESCRIPTION = false


/**
 * Show rule documentation when a [DocumentationURI]
 * in a violation tooltip is clicked.
 * 
 * <i>Show Inspection Description</i> also uses this.
 */
internal class RuleTooltipLinkHandler : TooltipLinkHandler() {
    
    override fun handleLink(refSuffix: String, editor: Editor) =
        when (refSuffix.isRuleSelector) {
            true -> PROCEED_TO_CALL_GET_DESCRIPTION
            else -> IGNORE_THIS_LINK
        }
    
    override fun getDescription(refSuffix: String, editor: Editor) = runBlocking {
        editor.project?.getRuleDocumentationByFullCode(refSuffix)?.toHTML()
    }
    
    override fun getDescriptionTitle(refSuffix: String, editor: Editor) =
        message("documentation.tooltips.ruleDocumentation.title")
    
}
