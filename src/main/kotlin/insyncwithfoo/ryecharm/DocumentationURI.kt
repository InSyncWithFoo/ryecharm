package insyncwithfoo.ryecharm

import com.intellij.platform.backend.documentation.DocumentationLinkHandler
import insyncwithfoo.ryecharm.ruff.documentation.RuffDocumentationLinkHandler
import java.net.URI
import java.net.URISyntaxException


internal enum class DocumentationURIHost(private val value: String) {
    RUFF_OPTION("ruff.option"),
    RUFF_RULE("ruff.rule");
    
    override fun toString() = value
    
    companion object {
        fun fromValue(value: String) =
            entries.find { value == it.value }
    }
}


/**
 * A custom RyeCharm URI, as used by documentation popups,
 * to be handled by [DocumentationLinkHandler]s.
 * 
 * @property path The URI's path, but without the preceding slash.
 * 
 * @see RuffDocumentationLinkHandler
 */
internal class DocumentationURI private constructor(val host: DocumentationURIHost, val path: String) {
    
    override fun toString() =
        "${RyeCharm.ID}://$host/$path"
    
    companion object {
        
        fun ruffOption(path: String) =
            DocumentationURI(DocumentationURIHost.RUFF_OPTION, path)
        
        fun ruffRule(path: String) =
            DocumentationURI(DocumentationURIHost.RUFF_RULE, path)
        
        fun parse(text: String): DocumentationURI? {
            val uri = try {
                URI(text)
            } catch (_: URISyntaxException) {
                return null
            }
            
            if (uri.scheme != RyeCharm.ID) {
                return null
            }
            
            val host = DocumentationURIHost.fromValue(uri.host) ?: return null
            
            return DocumentationURI(host, uri.path.removePrefix("/"))
        }
        
    }
    
}
