package insyncwithfoo.ryecharm.ruff.folding

import com.intellij.lang.folding.CustomFoldingBuilder
import insyncwithfoo.ryecharm.RyeCharm


/**
 * To be thrown by [CustomFoldingBuilder.getLanguagePlaceholderText]'s implementations
 * when a branch is believed to be unreachable.
 */
internal class CannotFold : RuntimeException(MESSAGE) {
    companion object {
        val MESSAGE = """
            |There's something wrong: PSI element cannot be folded due to data mismatching.
            |Please try to narrow the problem and report the bug at ${RyeCharm.ISSUE_TRACKER}.
        """.trimMargin()
    }
}
