package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.LinkResolveResult
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffDocumentationTarget
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffOptionDocumentationTarget
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffRuleDocumentationTarget


internal const val RUFF_OPTION_HOST = "ruff.option"
internal const val RUFF_RULE_HOST = "ruff.rule"


private typealias URL = String


/**
 * Show a new popup in-place when certain links
 * in documentation popups are clicked.
 */
internal class RuffDocumentationLinkHandler : DocumentationLinkHandler {
    
    override fun resolveLink(target: DocumentationTarget, url: URL): LinkResolveResult? {
        if (target !is RuffDocumentationTarget) {
            return null
        }
        
        return resolveLink(target, url)?.let {
            LinkResolveResult.resolvedTarget(it)
        }
    }
    
    private fun resolveLink(target: RuffDocumentationTarget, url: URL): RuffDocumentationTarget? {
        val uri = DocumentationURI.parse(url) ?: return null
        
        return when (uri.host) {
            RUFF_OPTION_HOST -> RuffOptionDocumentationTarget(target.element, uri.path)
            RUFF_RULE_HOST -> RuffRuleDocumentationTarget(target.element, uri.path)
            else -> null
        }
    }
    
}
