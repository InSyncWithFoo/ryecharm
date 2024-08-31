package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class RuleCommand(override val executable: Path, private val code: String) : Command(), RuffCommand {
    
    override val subcommand = "rule"
    override val timeoutKey = RuffTimeouts.RULE.key
    
    override val arguments: List<String>
        get() = listOf(code)
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.rule")
    
}
