package insyncwithfoo.ryecharm.common.logging

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project


private const val INDENTATION = "    "
private const val CLOSING_BRACE = "}"


private val String.isOutputLogLine: Boolean
    get() = this.startsWith(OUTPUT_LOG_LINE_PREFIX)


/**
 * Fold output lines in logging consoles.
 */
internal class RyeCharmCommandOutputFoldingBuilder : ConsoleFolding() {
    
    override fun isEnabledForConsole(consoleView: ConsoleView) =
        consoleView is RyeCharmLoggingConsole
    
    override fun shouldFoldLine(project: Project, line: String) =
        line.isOutputLogLine || line.startsWith(INDENTATION) || line == CLOSING_BRACE
    
    override fun shouldBeAttachedToThePreviousLine() = false
    
    /**
     * @see debug
     */
    override fun getPlaceholderText(project: Project, lines: MutableList<String>) =
        when (lines.isEmpty()) {
            true -> null
            else -> "${lines.first().substringBefore(':')}: ..."
        }
    
}
