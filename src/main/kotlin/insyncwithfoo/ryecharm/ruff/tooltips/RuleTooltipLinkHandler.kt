package insyncwithfoo.ryecharm.ruff.tooltips

import com.intellij.codeInsight.highlighting.TooltipLinkHandler
import com.intellij.openapi.editor.Editor
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationByFullCode
import insyncwithfoo.ryecharm.ruff.documentation.getRuleDocumentationByRuleName
import insyncwithfoo.ryecharm.ruff.documentation.isRuleSelector
import insyncwithfoo.ryecharm.toHTMLInReadAction
import kotlinx.coroutines.runBlocking


private const val PROCEED_TO_CALL_GET_DESCRIPTION = false


/**
 * Show rule documentation when a [DocumentationURI]
 * in a violation tooltip is clicked.
 * 
 * <i>Show Inspection Description</i> also uses this.
 */
internal class RuleTooltipLinkHandler : TooltipLinkHandler() {
    
    override fun handleLink(refSuffix: String, editor: Editor) =
        PROCEED_TO_CALL_GET_DESCRIPTION
    
    override fun getDescription(refSuffix: String, editor: Editor) = runBlocking {
        when (refSuffix.isRuleSelector) {
            true -> editor.project?.getRuleDocumentationByFullCode(refSuffix)?.toHTMLInReadAction()
            else -> editor.project?.getRuleDocumentationByRuleName(refSuffix)?.toHTMLInReadAction()
        }
    }
    
    override fun getDescriptionTitle(refSuffix: String, editor: Editor) =
        message("documentation.tooltips.ruleDocumentation.title")
    
}
