package insyncwithfoo.ryecharm.ruff.documentation.noqa

import com.intellij.psi.PsiComment


/**
 * Due to some reason, IntelliJ IDEA's moemory usage always peaks
 * when analyzing this file with injected regex fragments.
 * 
 * This allows bypassing the detection algorithm.
 */
private fun String.toRegexBypassingIDELanguageInjection() = this.toRegex()


// From:
// https://github.com/astral-sh/ruff/blob/dedefd73dac18ea112cea1254fea6388fe67237b/crates/ruff_linter/src/noqa.rs#L180
internal val noqaCode = """[A-Z]+[0-9]+""".toRegexBypassingIDELanguageInjection()

// From:
// https://github.com/astral-sh/ruff/blob/dedefd73dac18ea112cea1254fea6388fe67237b/crates/ruff_linter/src/noqa.rs#L56
//
// Things to note:
// * Ruff use Rust's `char.is_whitespace()` / `str.trim_end()`.
//   They are replaced with `\h` here for simplicity.
// * Whitespace/commas are technically not required between codes.
//   See: https://github.com/astral-sh/ruff/issues/12808
internal val noqaComment = """(?x)
    (?<prefix>\#\h*(?i:noqa))
    (?:
        (?<colon>:\h*)
        (?<codeList>$noqaCode(?:(?<lastSeparator>[\h,]*+)$noqaCode)*+)
    )?
""".toRegexBypassingIDELanguageInjection()


// https://github.com/astral-sh/ruff/blob/dedefd73dac18ea112cea1254fea6388fe67237b/crates/ruff_linter/src/noqa.rs#L436
private val fileNoqaComment = """(?x)
    \#
	\h*(?:flake8|ruff)\h*:
	\h*(?i:noqa)\h*
	(?::\h*(?<codeList>$noqaCode(?:[\h,]\h*$noqaCode)*)|$)
""".toRegexBypassingIDELanguageInjection()


internal typealias NoqaCode = String


private data class NoqaCodeFragment(
    val content: NoqaCode,
    val range: IntRange
) {
    override fun toString() = content
}


private fun NoqaCodeFragment(group: MatchResult, codeListAbsoluteOffset: Int): NoqaCodeFragment {
    val start = group.range.first + codeListAbsoluteOffset
    val end = group.range.last + codeListAbsoluteOffset
    
    return NoqaCodeFragment(group.value, start..end)
}


internal class NoqaComment private constructor(private val codes: List<NoqaCodeFragment>) {
    
    fun findNoqaCodeAtOffset(offset: Int): String? {
        return codes.find { offset in it.range }?.toString()
    }
    
    companion object {
        
        private fun fromMatchResult(match: MatchResult, elementOffset: Int): NoqaComment? {
            val delimitedCodeList = match.groups["codeList"] ?: return null
            
            val codeListOffsetRelativeToMatch = delimitedCodeList.range.first
            val matchOffsetRelativeToElement = match.range.first
            val codeListAbsoluteOffset =
                elementOffset + matchOffsetRelativeToElement + codeListOffsetRelativeToMatch
            
            val codes = noqaCode.findAll(delimitedCodeList.value)
                .map { NoqaCodeFragment(it, codeListAbsoluteOffset) }
            
            return NoqaComment(codes.toList())
        }
        
        private fun parseFileLevelComment(text: String, elementOffset: Int) =
            fileNoqaComment.find(text)?.let { fromMatchResult(it, elementOffset) }
        
        private fun parseLineComment(text: String, elementOffset: Int): NoqaComment? =
            noqaComment.find(text)?.let { fromMatchResult(it, elementOffset) }
        
        fun parse(psiComment: PsiComment): NoqaComment? {
            val text = psiComment.text
            val elementOffset = psiComment.textOffset
            
            return parseFileLevelComment(text, elementOffset)
                ?: parseLineComment(text, elementOffset)
        }
        
    }
    
}
