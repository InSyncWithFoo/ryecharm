package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.openapi.util.text.HtmlChunk
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val FORCE_LINEBREAK = "  "


internal typealias OptionName = String
internal typealias OptionDocumentation = HTML


private val sectionLinks: Regex
    get() {
        val text = """(?<text>[^\[\]]+)"""
        val target = """(?<target>[\w-]+)"""
        
        return """\[$text]\(#$target\)""".toRegex()
    }


/**
 * Before: `lint_flake8-annotations_allow-star-arg-any`
 * 
 * After: `lint.flake8-annotations.allow-star-arg-any`
 */
private fun String.anchorToTOMLPath() =
    this.replace("_", ".")


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
    
    val uri = DocumentationURI(RUFF_OPTION_HOST, target.anchorToTOMLPath())
    
    "[$text]($uri)"
}


private fun OptionName.toDefinitionBlock() =
    this.wrappedInCodeBlock("toml").toHTML().removeSurroundingTag("pre")


private val OptionInfo.renderedContentBlock: HTML
    get() = doc.replaceSectionLinksWithSpecializedURIs().toHTML()


private val OptionInfo.renderedDefaultValue: HTML
    get() = default.wrappedInCodeBlock("toml").replaceSectionLinksWithSpecializedURIs().toHTML()


private val OptionInfo.renderedValueType: HTML
    get() = "`$valueType`".toHTML()


private val OptionInfo.renderedDeprecationInformation: HTML?
    get() = deprecated?.formattedAsMarkdown()?.replaceSectionLinksWithSpecializedURIs()?.toHTML()


private val OptionInfo.renderedExampleBlock: HTML
    get() = example.wrappedInCodeBlock("toml").toHTML()


private fun OptionInfo.makeDocumentationPopup(name: OptionName) = popup {
    definition(name.toDefinitionBlock())
    
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
}


// Upstream issue: https://github.com/astral-sh/ruff/issues/12960
// Logic based on: https://github.com/astral-sh/ruff/blob/4881d32c/crates/ruff_workspace/src/options_base.rs#L395
internal fun OptionInfo.render(name: OptionName): HTML {
    val style = HtmlChunk.styleTag("th { white-space: pre; }")
    val body = this.makeDocumentationPopup(name)
    
    return "${style}${body}"
}
