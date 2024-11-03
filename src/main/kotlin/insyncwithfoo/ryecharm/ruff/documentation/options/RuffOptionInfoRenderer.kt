package insyncwithfoo.ryecharm.ruff.documentation.options

import com.intellij.openapi.util.text.HtmlChunk
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock


private const val FORCE_LINEBREAK = "  "


private val sectionLinks: Regex
    get() {
        val text = """(?<text>[^\[\]]+)"""
        val target = """(?<target>[\w-]+)"""
        
        return """\[$text]\(#$target\)""".toRegex()
    }


private fun OptionDeprecationInfo.formattedAsMarkdown(): Markdown {
    val since = this.since?.let { "(since $it)$FORCE_LINEBREAK" }
    val message = this.message
    
    return """
        ${since.orEmpty()}
        ${message.orEmpty()}
    """.trimIndent().trim()
}


// TODO: Handle `psi_element` link clicks in-place.
private fun Markdown.replaceSectionLinksWithFullURLs() = this.replace(sectionLinks) { match ->
    val text = match.groups["text"]!!.value
    val target = match.groups["target"]!!.value
    
    val url = """https://docs.astral.sh/ruff/settings/#${target}"""
    
    "[$text]($url)"
}


private fun OptionName.toDefinitionBlock() =
    this.wrappedInCodeBlock("toml").toHTML().removeSurroundingTag("pre")


private val OptionInfo.renderedContentBlock: HTML
    get() = doc.replaceSectionLinksWithFullURLs().toHTML()


private val OptionInfo.renderedDefaultValue: HTML
    get() = default.wrappedInCodeBlock("toml").replaceSectionLinksWithFullURLs().toHTML()


private val OptionInfo.renderedValueType: HTML
    get() = "`$valueType`".toHTML()


private val OptionInfo.renderedDeprecationInformation: HTML?
    get() = deprecated?.formattedAsMarkdown()?.replaceSectionLinksWithFullURLs()?.toHTML()


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
