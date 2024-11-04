package insyncwithfoo.ryecharm.ruff.documentation.options

import com.intellij.openapi.util.text.HtmlChunk
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock


private const val FORCE_LINEBREAK = "  "


internal const val RUFF_OPTION_URI_PREFIX = "ruff_option://"


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
    
    val uri = "${RUFF_OPTION_URI_PREFIX}${target.anchorToTOMLPath()}"
    
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
