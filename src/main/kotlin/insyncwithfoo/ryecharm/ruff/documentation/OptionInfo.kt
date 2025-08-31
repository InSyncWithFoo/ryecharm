package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.text.HtmlChunk
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.Popup
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val FORCE_LINEBREAK = "  "


/**
 * A Ruff option name of one of the following forms:
 * 
 * * `foo` (relative name)
 * * `ruff.foo` (absolute name)
 * * Empty string (root name)
 */
internal typealias OptionName = String


private val sectionLinks = """(?x)
    \[(?<text>[^\[\]]+)]
    \(\#(?<target>[\w-]+)\)
""".toRegex()


/**
 * A `<span>`-wrapped part of a rendered TOML key
 * in the "Definition" block:
 * 
 * ```html
 * <div class="definition">
 *   <span style="color:#cfa58f;">ruff</span>
 *   <span style="">.</span>
 *   <span style="color:#cfa58f;">lint</span>
 *   <span style="">.</span>
 *   <span style="color:#cfa58f;">fixable</span>
 * </div>
 * ```
 */
private val definitionHighlightedKeySegment = """(?x)
    <span\s+style="[^"<>]+"\s*>
        [a-z0-9-]+
    </span>
""".toRegex()



/**
 * Before: `lint_flake8-annotations_allow-star-arg-any`
 * 
 * After: `lint.flake8-annotations.allow-star-arg-any`
 * 
 * @see tomlPathToAnchor
 */
private fun String.anchorToTOMLPath() =
    this.replace("_", ".")


/**
 * Before: `lint.flake8-annotations.allow-star-arg-any`
 *
 * After: `lint_flake8-annotations_allow-star-arg-any`
 * 
 * @see anchorToTOMLPath
 */
private fun OptionName.tomlPathToAnchor() =
    this.replace(".", "_")


internal fun OptionName.toAbsoluteName(): OptionName =
    when (this.isEmpty()) {
        true -> "ruff"
        else -> "ruff.$this"
    }


@Serializable
internal data class OptionDeprecationInfo(
    val since: String?,
    val message: String?
)


@Serializable
internal data class OptionInfo(
    val doc: String,
    val default: String,
    @SerialName("value_type")
    val valueType: String,
    val scope: String?,
    val example: String,
    val deprecated: OptionDeprecationInfo?
)


private fun OptionDeprecationInfo.formattedAsMarkdown(): Markdown {
    val since = this.since?.let { "(since $it)$FORCE_LINEBREAK" }
    val message = this.message
    
    return """
        ${since.orEmpty()}
        ${message.orEmpty()}
    """.trimIndent().trim()
}


private fun Markdown.replaceSectionLinksWithSpecializedURIs() = this.replace(sectionLinks) { match ->
    val text = match.groups["text"]!!.value
    val target = match.groups["target"]!!.value
    
    val uri = DocumentationURI.ruffOption(target.anchorToTOMLPath())
    
    "[$text]($uri)"
}


private fun OptionName.toDefinitionBlock(): HTML {
    val fragments = this.split(".")
    val html = this.wrappedInCodeBlock("toml").toHTML().removeSurroundingTag("pre")
    
    var index = 0
    
    return html.replace(definitionHighlightedKeySegment) {
        if (index == fragments.lastIndex) {
            return@replace it.value
        }
        
        val prefix = fragments.slice(1..index).joinToString(".")
        val uri = DocumentationURI.ruffOption(prefix)
        
        index++
        
        """<a href="$uri">${it.value}</a>"""
    }
}


private fun Popup.linkToDocsFooter(name: OptionName) = bottom {
    val anchor = when (name.isEmpty()) {
        true -> ""
        else -> "#${name.tomlPathToAnchor()}"
    }
    val url = "https://docs.astral.sh/ruff/settings/$anchor"
    
    // TODO: More ergonomic API
    icon("${AllIcons.Toolwindows::class.qualifiedName}.WebToolWindow")
    html("&nbsp;")
    html("""<a href="$url"><code>$name</code></a>""")
}


private val OptionInfo.renderedContentBlock: HTML
    get() = doc
        .replaceSectionLinksWithSpecializedURIs()
        .replaceRuleLinksWithSpecializedURIs()
        .toHTML()


private val OptionInfo.renderedDefaultValue: HTML
    get() = default.wrappedInCodeBlock("toml").replaceSectionLinksWithSpecializedURIs().toHTML()


private val OptionInfo.renderedValueType: HTML
    get() = "`$valueType`".toHTML()


private val OptionInfo.renderedDeprecationInformation: HTML?
    get() = deprecated?.formattedAsMarkdown()?.replaceSectionLinksWithSpecializedURIs()?.toHTML()


private val OptionInfo.renderedExampleBlock: HTML
    get() = example.wrappedInCodeBlock("toml").toHTML()


private fun OptionInfo.makeDocumentationPopup(name: OptionName) = popup {
    definition(name.toAbsoluteName().toDefinitionBlock())
    
    separator()
    
    content(renderedContentBlock)
    
    sections {
        default(renderedDefaultValue)
        type(renderedValueType)
        
        renderedDeprecationInformation?.let {
            deprecated(it)
        }
        
        example(renderedExampleBlock)
    }
    
    linkToDocsFooter(name)
}


// Upstream issue: https://github.com/astral-sh/ruff/issues/12960
// Logic based on: https://github.com/astral-sh/ruff/blob/4881d32c/crates/ruff_workspace/src/options_base.rs#L395
internal fun OptionInfo.render(name: OptionName): HTML {
    val style = HtmlChunk.styleTag("th { white-space: pre; }")
    val body = this.makeDocumentationPopup(name)
    
    return "${style}${body}"
}


private fun Map<OptionName, OptionInfo>.makeDocumentationPopup(name: OptionName) = popup {
    val list = entries.joinToString("\n") { (childName, _) ->
        val path = when (name.isEmpty()) {
            true -> childName
            else -> "${name}.${childName}"
        }
        val uri = DocumentationURI.ruffOption(path)
        
        "* [`${childName}`]($uri)"
    }
    
    val content = when (name.isEmpty()) {
        true -> message("documentation.popup.configGroupInfo.root", list)
        else -> message("documentation.popup.configGroupInfo", name, list)
    }
    
    definition(name.toAbsoluteName().toDefinitionBlock())
    
    separator()
    
    content(content.toHTML())
    
    linkToDocsFooter(name)
}


internal fun Map<OptionName, OptionInfo>.render(name: OptionName) =
    makeDocumentationPopup(name).toString()
