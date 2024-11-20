package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.LinkResolveResult
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffDocumentationTarget
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffOptionDocumentationTarget


internal const val RUFF_OPTION_HOST = "ruff.option"


private typealias URL = String


private fun URL.toRuffOptionURI() =
    DocumentationURI.parse(this)?.takeIf { it.host == RUFF_OPTION_HOST }


/**
 * Show a new popup in-place when certain links
 * in documentation popups are clicked.
 */
internal class RuffDocumentationLinkHandler : DocumentationLinkHandler {
    
    override fun resolveLink(target: DocumentationTarget, url: String): LinkResolveResult? {
        if (target !is RuffDocumentationTarget) {
            return null
        }
        
        return resolveLink(target, url)?.let {
            LinkResolveResult.resolvedTarget(it)
        }
    }
    
    private fun resolveLink(target: RuffDocumentationTarget, url: URL): RuffDocumentationTarget? {
        val newOption = url.toRuffOptionURI()?.path ?: return null
        
        return RuffOptionDocumentationTarget(target.element, newOption)
    }
    
}
