package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.LinkResolveResult
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.DocumentationURIHost
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffDocumentationTarget
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffOptionDocumentationTarget
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffRuleDocumentationTarget


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
            DocumentationURIHost.RUFF_OPTION -> RuffOptionDocumentationTarget(target.element, uri.path)
            DocumentationURIHost.RUFF_RULE -> RuffRuleDocumentationTarget(target.element, uri.path)
        }
    }
    
}
