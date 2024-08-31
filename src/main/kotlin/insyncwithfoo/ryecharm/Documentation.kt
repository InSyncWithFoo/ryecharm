package insyncwithfoo.ryecharm

import com.intellij.markdown.utils.doc.DocMarkdownToHtmlConverter
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.platform.backend.documentation.DocumentationResult


internal typealias Markdown = String
internal typealias HTML = String


// Upstream issue: https://youtrack.jetbrains.com/issue/IJPL-160938
// TODO: Switch to org.intellij.markdown.parser.MarkdownParser
/**
 * Converts a piece of Markdown to HTML
 * using [DocMarkdownToHtmlConverter].
 *
 * Note that, due to a bug in `DocMarkdownToHtmlConverter`,
 * nested code blocks are not rendered correctly.
 * Increasing outer code fence levels does not help.
 */ 
internal fun markdownToHTML(markdown: Markdown): HTML =
    DocMarkdownToHtmlConverter.convert(defaultProject, markdown)


internal fun HTML.toDocumentationResult() =
    DocumentationResult.documentation(this)


internal fun Markdown.wrappedInCodeBlock(language: String): Markdown =
    "```${language}\n${this}\n```"


internal fun HTML.removeSurroundingTag(tag: String): HTML =
    this.removeSurrounding("<${tag}>", "</${tag}>")


// com.intellij.lang.documentation.DocumentationMarkup
private const val DEFINITION = "definition"
private const val CONTENT = "content"
private const val SECTIONS = "sections"
private const val SECTION = "section"
private const val BOTTOM = "bottom"


internal abstract class Element(tagName: String) {
    
    private var element = HtmlChunk.tag(tagName)
    
    override fun toString() = element.toString()
    
    fun attr(name: String, value: Any) {
        element = element.attr(name, value.toString())
    }
    
    fun child(child: Element) {
        element = element.child(child.element)
    }
    
    inline fun <reified C : Element> child(createElement: () -> C, block: C.() -> Unit) {
        child(createElement().apply(block))
    }
    
    fun text(text: String) {
        element = element.addText(text)
    }
    
    fun html(text: HTML) {
        element = element.addRaw(text)
    }
    
}


internal class Popup : Element("div") {
    
    fun definition(rendered: HTML) {
        child(::Definition) { html(rendered) }
    }
    
    fun separator() {
        child(::Separator) {}
    }
    
    fun content(rendered: HTML) {
        child(::Content) { html(rendered) }
    }
    
    fun sections(block: Sections.() -> Unit) {
        child(::SectionsWrapper) {
            child(::Sections, block)
        }
    }
    
    fun bottom(block: Bottom.() -> Unit) {
        child(::Bottom, block)
    }
    
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.DEFINITION_ELEMENT
 */
internal class Definition : Element("div") {
    init {
        attr("class", DEFINITION)
    }
}


internal class Separator : Element("hr")


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.CONTENT_ELEMENT
 */
internal class Content : Element("div") {
    init {
        attr("class", CONTENT)
    }
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.SECTIONS_TABLE
 */
internal class SectionsWrapper : Element("table") {
    init {
        attr("class", SECTIONS)
    }
}


internal class Sections : Element("tbody") {
    
    private fun section(header: HTML, content: HTML) {
        child(::Section) {
            header(header)
            content(content)
        }
    }
    
    fun default(value: HTML) {
        section(message("documentation.popup.defaultValue"), value)
    }
    
    fun type(value: HTML) {
        section(message("documentation.popup.valueType"), value)
    }
    
    fun deprecated(message: HTML) {
        section(message("documentation.popup.deprecated"), message)
    }
    
    fun example(example: HTML) {
        section(message("documentation.popup.exampleUsage"), example)
    }
    
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.SECTION_HEADER_START
 */
internal class Section : Element("tr") {
    
    fun header(text: HTML) {
        child(::SectionHeader) { html("<p>$text</p>") }
    }
    
    fun content(text: HTML) {
        child(::SectionContent) { html("<p>$text</p>") }
    }
    
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.SECTION_HEADER_CELL
 */
private class SectionHeader : Element("td") {
    init {
        attr("scope", "row")
        attr("align", "left")
        attr("valign", "top")
        attr("class", SECTION)
    }
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.SECTION_CONTENT_CELL
 */
private class SectionContent : Element("td")


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.BOTTOM_ELEMENT
 */
internal class Bottom : Element("div") {
    
    init {
        attr("class", BOTTOM)
    }
    
    fun icon(src: String) {
        child(BottomIcon(src))
    }
    
}


/**
 * @see com.intellij.lang.documentation.DocumentationMarkup.INFORMATION_ICON
 */
internal class BottomIcon(src: String) : Element("icon") {
    init {
        attr("src", src)
    }
}


internal fun popup(block: Popup.() -> Unit) = Popup().apply(block)
