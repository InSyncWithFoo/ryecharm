package insyncwithfoo.ryecharm.ruff

import com.intellij.psi.PsiComment


internal typealias RuleCode = String


/**
 * Due to some reason, IntelliJ IDEA's memory usage always peaks
 * when analyzing this file with injected regex fragments.
 * 
 * This allows bypassing the detection algorithm.
 */
private fun String.toRegexBypassingIDELanguageInjection() = this.toRegex()


// From:
// https://github.com/astral-sh/ruff/blob/5c548dcc04/crates/ruff_linter/src/noqa.rs#L180
internal val ruleCode = """[A-Z]+[0-9]+""".toRegexBypassingIDELanguageInjection()

// From:
// https://github.com/astral-sh/ruff/blob/5c548dcc04/crates/ruff_linter/src/noqa.rs#L56
//
// Ruff uses Rust's `char.is_whitespace()` / `str.trim_end()`.
// They are replaced with `\h` here for simplicity.
internal val noqaComment = """(?x)
    (?<prefix>
        \#\h*
        (?i:noqa)
    )
    (?:
        (?<colon>:(?:\h+(?=$ruleCode|$))?)
        (?<codeList>
            $ruleCode
            (?:(?<lastSeparator>[\h,]*+)$ruleCode)*+
        )?
    )?
""".toRegexBypassingIDELanguageInjection()


// From:
// https://github.com/astral-sh/ruff/blob/5c548dcc04/crates/ruff_linter/src/noqa.rs#L437
//
// File-level comments actually uses `[A-Z]+[A-Za-z0-9]+` for codes,
// but this doesn't seem to be a good choice.
private val fileNoqaComment = """(?x)
    (?<prefix>
        ^\#\h*
        (?:flake8|ruff)\h*:\h*
        (?i:noqa)
    )
	(?:
        (?<colon>\h*:(?:\h+(?=$ruleCode|$))?)
        (?<codeList>
            $ruleCode
            (?:(?<lastSeparator>[\h,]\h*)$ruleCode)*
        )?
    )?
""".toRegexBypassingIDELanguageInjection()


private class Fragment(val content: String, val start: Int) {
    
    val end: Int
        get() = start + content.length
    
    val range: IntRange
        get() = start..<end
    
    val inclusiveRange: IntRange
        get() = start..end
    
    override fun toString() = content
    
}


private fun Fragment(group: MatchGroup, elementOffset: Int): Fragment {
    val groupOffsetRelativeToElement = group.range.first
    
    return Fragment(group.value, elementOffset + groupOffsetRelativeToElement)
}


private operator fun Fragment.get(subfragment: Fragment): String {
    val shiftedRange = (subfragment.start - this.start)..<(subfragment.end - this.end)
    
    return content.slice(shiftedRange)
}


private operator fun Fragment.get(subfragmentRange: IntRange): String {
    val shiftedRange = (subfragmentRange.first - this.start)..(subfragmentRange.last - this.start)
    
    return content.slice(shiftedRange)
}


/**
 * A `# noqa` comment either located within a document or newly constructed.
 *
 * All offsets in question are absolute (i.e., relative to the start of the document).
 */
internal class NoqaComment private constructor(
    private val prefix: Fragment,
    private val colon: Fragment?,
    private val codes: List<Fragment>,
    private val lastSeparator: String?,
    private val fileLevel: Boolean,
    private val original: Fragment
) {
    
    val start by original::start
    val end by original::end
    val range by original::range
    
    val codesAsStrings: List<String>
        get() = codes.mapTo(mutableListOf()) { it.content }
    
    val separator: String
        get() = lastSeparator ?: ", "
    
    fun findCodeAtOffset(offset: Int) =
        codes.firstNotNullOfOrNull { code ->
            code.content.takeIf { offset in code.inclusiveRange }
        }
    
    fun withNewCode(code: RuleCode) = when {
        colon == null -> "$prefix: $code"
        
        codes.isEmpty() -> {
            val padding = when (colon.content.endsWith(' ')) {
                true -> ""
                else -> " "
            }
            
            "${prefix}${colon}${padding}${code}"
        }
        
        else -> "${this}${separator}${code}"
    }
    
    fun withoutCode(code: RuleCode): String {
        if (colon == null || codes.isEmpty()) {
            return this.toString()
        }
        
        val toBeRetained = codes.withIndex().filter { (_, existing) ->
            existing.content != code
        }
        
        if (toBeRetained.isEmpty()) {
            return ""
        }
        
        val newText = StringBuilder("${prefix}${colon}")
        
        for ((index, pair) in toBeRetained.withIndex()) {
            val (codeListIndex, existingCode) = pair
            
            val previousDelimiterStart = when (codeListIndex == 0 || index == 0) {
                true -> existingCode.start
                else -> codes[codeListIndex - 1].end
            }
            
            newText.append(original[previousDelimiterStart..<existingCode.end])
        }
        
        return newText.toString()
    }
    
    override fun toString() = original.content
    
    companion object {
        
        fun parse(psiComment: PsiComment): NoqaComment? {
            val text = psiComment.text
            val elementOffset = psiComment.textOffset
            
            return parse(text, elementOffset)
        }
        
        fun fromCode(code: RuleCode) =
            parse("# noqa: $code")
        
        fun parse(text: String, elementOffset: Int = 0) =
            parseFileLevelComment(text, elementOffset) ?: parseLineComment(text, elementOffset)
        
        private fun parseFileLevelComment(text: String, elementOffset: Int) =
            fileNoqaComment.find(text)?.let { fromMatchResult(it, elementOffset, fileLevel = true) }
        
        private fun parseLineComment(text: String, elementOffset: Int) =
            noqaComment.find(text)?.let { fromMatchResult(it, elementOffset, fileLevel = false) }
        
        private fun fromMatchResult(match: MatchResult, elementOffset: Int, fileLevel: Boolean): NoqaComment {
            val prefix = match.groups["prefix"]!!
            val colon = match.groups["colon"]
            val codeList = match.groups["codeList"]
            val lastSeparator = match.groups["lastSeparator"]
            
            return NoqaComment(
                prefix = Fragment(prefix, elementOffset),
                colon = colon?.let { Fragment(it, elementOffset) },
                codes = collectCodes(codeList, elementOffset),
                lastSeparator = lastSeparator?.value,
                fileLevel = fileLevel,
                original = Fragment(match.groups[0]!!, elementOffset)
            )
        }
        
        private fun collectCodes(codeListGroup: MatchGroup?, elementOffset: Int): List<Fragment> {
            if (codeListGroup == null) {
                return emptyList()
            }
            
            val codeListOffsetRelativeToElement = codeListGroup.range.first
            val codeListAbsoluteOffset = elementOffset + codeListOffsetRelativeToElement
            
            return ruleCode.findAll(codeListGroup.value).mapTo(mutableListOf()) {
                Fragment(it.groups[0]!!, codeListAbsoluteOffset)
            }
        }
        
    }
    
}
