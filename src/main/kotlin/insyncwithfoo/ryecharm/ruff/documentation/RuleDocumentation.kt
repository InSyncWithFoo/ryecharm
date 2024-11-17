package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toHTML


private val optionsSection = """(?mx)
    ^\#\#\h*Options\n
    (?:[\s\S](?!\n\#))*
""".trimIndent().toRegex()


private val optionNameInListItem = """(?m)(?<prefix>^[-*]\h*\[?)`(?<path>[A-Za-z0-9.-]+)`""".toRegex()


// https://github.com/astral-sh/ruff/issues/14348
/**
 * Replace option names with links to specialized URIs.
 */
private fun String.insertOptionLinks() = this.replace(optionsSection) {
    it.value.replace(optionNameInListItem) { match ->
        val prefix = match.groups["prefix"]!!.value
        val path = match.groups["path"]!!.value
        
        val uri = DocumentationURI(RUFF_OPTION_HOST, path)
        
        "$prefix[`$path`]($uri)"
    }
}


private suspend fun Project.getMarkdownDocumentationForRule(rule: String): Markdown? {
    val ruff = this.ruff ?: return null
    val command = ruff.rule(rule)
    
    val output = ProgressContext.IO.compute {
        runInBackground(command)
    }
    
    if (output.isTimeout) {
        processTimeout(command)
        return null
    }
    
    if (output.isCancelled || !output.isSuccessful) {
        return null
    }
    
    return output.stdout
}


internal suspend fun Project.getDocumentationForRule(rule: String): HTML? {
    val markdownDocumentation = getMarkdownDocumentationForRule(rule) ?: return null
    
    return readAction { markdownDocumentation.insertOptionLinks().toHTML() }
}
