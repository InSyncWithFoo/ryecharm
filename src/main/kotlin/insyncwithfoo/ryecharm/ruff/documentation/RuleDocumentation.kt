package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.CachedResult
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.ruleCode
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toHTML


private val optionsSection = """(?mx)
    ^\#\#\h*Options\n
    (?:[\s\S](?!\n\#))*
""".trimIndent().toRegex()


private val optionNameInListItem = """(?m)(?<prefix>^[-*]\h*\[?)`(?<path>[A-Za-z0-9.-]+)`""".toRegex()
private val ruleLink = """https://docs\.astral\.sh/ruff/rules/(?<rule>[a-z-]+)""".toRegex()


private val String.isRuleCode: Boolean
    get() = ruleCode.matchEntire(this) != null


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


private fun String.replaceRuleLinksWithSpecializedURIs() = this.replace(ruleLink) {
    val rule = it.groups["rule"]!!.value
    val uri = DocumentationURI(RUFF_RULE_HOST, rule)
    
    uri.toString()
}


private suspend fun Project.getNewRuleNameToCodeMap(): Map<RuleCode, String>? {
    val ruff = this.ruff ?: return null
    val command = ruff.allRules()
    
    val output = ProgressContext.IO.compute {
        runInBackground(command)
    }
    
    if (output.completedAbnormally) {
        return null
    }
    
    val rules = output.stdout.parseAsJSON<List<RuleInfo>>()
    
    return rules?.associate { it.name to it.code }
}


private suspend fun Project.getCodeForRuleName(name: String): String? {
    val ruff = this.ruff ?: return null
    val executable = ruff.executable
    
    val cache = RuffCache.getInstance(this)
    val cached = cache.ruleNameToCodeMap
    
    if (cached?.matches(executable) == true) {
        return cached.result[name]
    }
    
    val newData = getNewRuleNameToCodeMap()?.also {
        cache.ruleNameToCodeMap = CachedResult(it, executable)
    }
    
    return newData?.get(name)
}


private suspend fun Project.getRuleMarkdownDocumentation(rule: String): Markdown? {
    val code = when (rule.isRuleCode) {
        true -> rule
        else -> getCodeForRuleName(rule) ?: return null
    }
    
    val ruff = this.ruff ?: return null
    val command = ruff.rule(code)
    
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


internal suspend fun Project.getRuleDocumentation(rule: String): HTML? {
    val markdownDocumentation = getRuleMarkdownDocumentation(rule) ?: return null
    
    return readAction {
        markdownDocumentation
            .insertOptionLinks()
            .replaceRuleLinksWithSpecializedURIs()
            .toHTML()
    }
}
