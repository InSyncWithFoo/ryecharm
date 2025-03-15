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


internal val ruleCode = """(?>[A-Z]+[0-9]+)""".toRegexBypassingIDELanguageInjection()

internal val notFollowedByInvalidSuffix = """(?!$ruleCode*+(?!$|[\h\#,]))""".toRegexBypassingIDELanguageInjection()

// From:
// https://github.com/astral-sh/ruff/blob/27e9d1fe3e/crates/ruff_linter/src/noqa.rs#L373-L605
//
// Ruff uses Rust's `char.is_whitespace()`;
// it is replaced with `\h` here for simplicity.
//
// FIXME: Pare this down a bit
internal val noqaComment = """(?x)
    (?<prefix>
        \#\h*
        (?<fileLevelPrefix>(?:flake8|ruff)\h*:\h*)?+
        (?i:noqa)
    )
    (?:
        (?=$|\#|\h++(?!:))
    |
        (?<colon>\h*:(?:\h+(?=$|$ruleCode))?+)
        (?<codeList>
            (?!$ruleCode)
        |
            $ruleCode$notFollowedByInvalidSuffix
            (?>
                (?=[\h,]*+$ruleCode$notFollowedByInvalidSuffix)
                (?<lastSeparator>\h++|,\h*+)*+
                $ruleCode$notFollowedByInvalidSuffix
            )*
        )
    )
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
        
        fun parse(text: String, elementOffset: Int = 0): NoqaComment? {
            val match = noqaComment.find(text) ?: return null
            
            val prefix = match.groups["prefix"]!!
            val colon = match.groups["colon"]
            val codeList = match.groups["codeList"]
            val lastSeparator = match.groups["lastSeparator"]
            val fileLevelPrefix = match.groups["fileLevelPrefix"]
            
            return NoqaComment(
                prefix = Fragment(prefix, elementOffset),
                colon = colon?.let { Fragment(it, elementOffset) },
                codes = collectCodes(codeList, elementOffset),
                lastSeparator = lastSeparator?.value,
                fileLevel = fileLevelPrefix != null,
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
