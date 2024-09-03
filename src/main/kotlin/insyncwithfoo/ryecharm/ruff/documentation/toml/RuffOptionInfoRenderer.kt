package insyncwithfoo.ryecharm.ruff.documentation.toml

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.HtmlChunk
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.markdownToHTML
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.wrappedInCodeBlock


private const val FORCE_LINEBREAK = "  "


private val sectionLinks: Regex
    get() {
        val text = """(?<text>[^\[\]]+)"""
        val target = """(?<target>[\w-]+)"""
        
        return """\[$text]\(#$target\)""".toRegex()
    }


private fun OptionDeprecationInfo.formattedAsMarkdown(): Markdown {
    val since = this.since?.let { "(since $it)${FORCE_LINEBREAK}" }
    val message = this.message
    
    return """
        ${since.orEmpty()}
        ${message.orEmpty()}
    """.trimIndent().trim()
}


// Upstream issue: https://github.com/astral-sh/ruff/issues/12960
// Logic based on: https://github.com/astral-sh/ruff/blob/4881d32c/crates/ruff_workspace/src/options_base.rs#L395
internal class OptionInfoRenderer(private val project: Project) {
    
    fun render(optionName: OptionName, optionInfo: OptionInfo): HTML {
        val style = HtmlChunk.styleTag("th { white-space: pre; }")
        val body = documentationPopupForOption(optionName, optionInfo)
        
        return "${style}${body}"
    }
    
    // TODO: Handle `psi_element` link clicks in-place.
    private fun Markdown.replaceSectionLinksWithFullURLs() = this.replace(sectionLinks) { match ->
        val text = match.groups["text"]!!.value
        val target = match.groups["target"]!!.value
        
        val url = """https://docs.astral.sh/ruff/settings/#${target}"""
        
        "[$text]($url)"
    }
    
    private fun documentationPopupForOption(name: OptionName, info: OptionInfo) = popup {
        val definition = name.wrappedInCodeBlock("toml")
            .let { markdownToHTML(it) }
            .removeSurroundingTag("pre")
        
        val content = info.doc
            .replaceSectionLinksWithFullURLs()
            .let { markdownToHTML(it) }
        
        val default = info.default.wrappedInCodeBlock("toml")
            .replaceSectionLinksWithFullURLs()
            .let { markdownToHTML(it) }
        
        val type = info.valueType
            .let { "`$it`" }
            .let { markdownToHTML(it) }
        
        val deprecated = info.deprecated?.formattedAsMarkdown()
            ?.replaceSectionLinksWithFullURLs()
            ?.let { markdownToHTML(it) }
        
        val example = info.example.wrappedInCodeBlock("toml")
            .let { markdownToHTML(it) }
        
        definition(definition)
        
        separator()
        
        content(content)
        
        sections {
            default(default)
            type(type)
            
            if (deprecated != null) {
                deprecated(deprecated)
            }
            
            example(example)
        }
    }
    
}
