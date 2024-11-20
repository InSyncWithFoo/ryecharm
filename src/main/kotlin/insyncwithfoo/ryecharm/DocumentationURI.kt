package insyncwithfoo.ryecharm

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import insyncwithfoo.ryecharm.ruff.documentation.RuffDocumentationLinkHandler
import java.net.URI
import java.net.URISyntaxException


/**
 * A custom RyeCharm URI, as used by documentation popups,
 * to be handled by [DocumentationLinkHandler]s.
 * 
 * @property path The URI's path, but without the preceding slash.
 * 
 * @see RuffDocumentationLinkHandler
 */
internal class DocumentationURI(val host: String, val path: String) {
    
    override fun toString() =
        "${RyeCharm.ID}://$host/$path"
    
    companion object {
        fun parse(text: String): DocumentationURI? {
            val uri = try {
                URI(text)
            } catch (_: URISyntaxException) {
                return null
            }
            
            if (uri.scheme != RyeCharm.ID) {
                return null
            }
            
            return DocumentationURI(uri.host, uri.path.removePrefix("/"))
        }
    }
    
}
