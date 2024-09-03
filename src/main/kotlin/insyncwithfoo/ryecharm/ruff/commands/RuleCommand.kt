package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class RuleCommand : Command(), RuffCommand {
    
    override val subcommand = "rule"
    override val timeoutKey = RuffTimeouts.RULE.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.rule")
    
}
