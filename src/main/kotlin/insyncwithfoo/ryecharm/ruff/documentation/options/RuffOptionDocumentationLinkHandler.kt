package insyncwithfoo.ryecharm.ruff.documentation.options

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.LinkResolveResult


/**
 * Show a new popup in-place when `ruff_option://` links
 * in documentation popups for [RuffOptionDocumentationTarget]s
 * are clicked.
 * 
 * @see replaceSectionLinksWithSpecializedURIs
 */
internal class RuffOptionDocumentationLinkHandler : DocumentationLinkHandler {
    
    override fun resolveLink(target: DocumentationTarget, url: String): LinkResolveResult? {
        if (target !is RuffOptionDocumentationTarget) {
            return null
        }
        
        if (!url.startsWith(RUFF_OPTION_URI_PREFIX)) {
            return null
        }
        
        val newOption = url.removePrefix(RUFF_OPTION_URI_PREFIX)
        val newTarget = target.withOption(newOption)
        
        return LinkResolveResult.resolvedTarget(newTarget)
    }
    
}
