package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.CachedResult
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.commands.allRulesInfo
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.commands.ruleInfo
import insyncwithfoo.ryecharm.ruff.ruleCode
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runUnderIOThread


/**
 * Either a full code (e.g., `RUF100`) or a prefix (e.g., `RUF`, `RUF1`, `RUF10`).
 */
internal typealias RuleSelector = String

/**
 * A rule name (e.g., `unused-noqa`).
 */
internal typealias RuleName = String

/**
 * Either a [RuleSelector] or a [RuleName].
 */
internal typealias RuleSelectorOrName = String


private const val ALL: RuleSelector = "ALL"


/**
 * A fork of [insyncwithfoo.ryecharm.ruff.ruleCode].
 */
internal val ruleSelector = """(?<linter>[A-Z]+)(?<number>[0-9]*)""".toRegex()


private val enabledRulesArray = """(?x)
    linter\.rules\.enabled\h*=\h*\[(?<list>[^\[\]]*)]
""".toRegex()


private val ruleNameAndCode = """(?mx)
    ^\h*(?<name>[a-z0-9-]+)\h+\((?<code>$ruleCode)\),?\h*$
""".toRegex()


private val optionsSection = """(?mx)
    ^\#\#\h*Options\n
    (?:[\s\S](?!\n\#))*
""".trimIndent().toRegex()


private val optionNameInListItem = """(?m)(?<prefix>^[-*]\h*\[?)`(?<path>[A-Za-z0-9.-]+)`""".toRegex()


// https://github.com/astral-sh/ruff/blob/977447f9b8/scripts/check_docs_formatted.py#L19-L24
private val optionPseudoLinkOrCodeBlock = """(?mx)
    ^```\h*+\w*+\h*+\n
    (?s:.*?)\n
    ```\h*$
    |
    \[`(?<name>[a-z0-9][a-z0-9.-]*)`](?![\[(])
""".toRegex()


private val ruleLink = """https://docs\.astral\.sh/ruff/rules/(?<rule>[a-z-]+)/?""".toRegex()


internal val String.isRuleSelector: Boolean
    get() = ruleSelector.matchEntire(this) != null


internal val RuleSelectorOrName.isPylintCodePrefix: Boolean
    get() = this in listOf("PLC", "PLE", "PLR", "PLW")


// https://github.com/astral-sh/ruff/issues/14348
/**
 * Replace option names with links to specialized URIs.
 */
private fun Markdown.insertOptionLinks() = this.replace(optionsSection) {
    it.value.replace(optionNameInListItem) { match ->
        val prefix = match.groups["prefix"]!!.value
        val path = match.groups["path"]!!.value
        
        val uri = DocumentationURI.ruffOption(path)
        
        "$prefix[`$path`]($uri)"
    }
}


private fun Markdown.replaceOptionPseudoLinksWithActualLinks() = this.replace(optionPseudoLinkOrCodeBlock) {
    when (val name = it.groups["name"]?.value) {
        null -> it.value
        else -> {
            val uri = DocumentationURI.ruffOption(name)
            "[`$name`]($uri)"
        }
    }
}


internal fun Markdown.replaceRuleLinksWithSpecializedURIs() = this.replace(ruleLink) {
    val rule = it.groups["rule"]!!.value
    val uri = DocumentationURI.ruffRule(rule)
    
    uri.toString()
}


private suspend fun Project.getNewRuleNameToCodeMap(): Map<RuleName, RuleCode>? {
    val ruff = this.ruff ?: return null
    val command = ruff.allRulesInfo()
    
    val output = runUnderIOThread {
        runInBackground(command)
    }
    
    if (output.completedAbnormally) {
        return null
    }
    
    val rules = output.stdout.parseAsJSON<List<RuleInfo>>()
    
    return rules?.associate { it.name to it.code }
}


internal suspend fun Project.getRuleNameToCodeMap(): Map<RuleName, RuleCode>? {
    val ruff = this.ruff ?: return null
    val executable = ruff.executable
    
    val cache = RuffCache.getInstance(this)
    val cached = cache.ruleNameToCodeMap
    
    if (cached?.matches(executable) == true) {
        return cached.result
    }
    
    val newData = getNewRuleNameToCodeMap()?.also {
        cache.ruleNameToCodeMap = CachedResult(it, executable)
    }
    
    return newData
}


internal suspend fun Project.getRuleDocumentationByFullCode(code: RuleCode): Markdown? {
    val ruff = this.ruff ?: return null
    val command = ruff.ruleInfo(code)
    
    val output = runUnderIOThread {
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
        .insertOptionLinks()
        .replaceOptionPseudoLinksWithActualLinks()
        .replaceRuleLinksWithSpecializedURIs()
}


internal suspend fun Project.getRuleDocumentationByRuleName(name: RuleName) =
    getRuleNameToCodeMap()?.get(name)?.let {
        getRuleDocumentationByFullCode(it)
    }


private suspend fun Project.getEnabledRules(selector: RuleSelector): Map<RuleName, RuleCode>? {
    val ruff = this.ruff ?: return null
    val command = ruff.showSettings(select = listOf(selector))
    
    val output = runUnderIOThread {
        runInBackground(command)
    }
    
    if (output.completedAbnormally) {
        return null
    }
    
    val (array) = enabledRulesArray.find(output.stdout)?.destructured ?: return null
    
    return ruleNameAndCode.findAll(array).associate {
        val (name, code) = it.destructured
        Pair(name, code)
    }
}


internal suspend fun Project.getRuleListBySelector(selector: RuleSelector): Markdown? {
    if (selector == ALL) {
        return message("documentation.popup.ruleList.all")
    }
    
    val enabledRules = getEnabledRules(selector) ?: return null
    
    if (enabledRules.isEmpty()) {
        return null
    }
    
    val ruleList = StringBuilder()
    
    for ((name, code) in enabledRules) {
        val uri = DocumentationURI.ruffRule(code)
        ruleList.append("\n* [`$name`]($uri) (`$code`)")
    }
    
    return when (ruleList.isEmpty()) {
        true -> message("documentation.popup.ruleList.empty", selector)
        else -> message("documentation.popup.ruleList", selector, enabledRules.size, ruleList)
    }
}
