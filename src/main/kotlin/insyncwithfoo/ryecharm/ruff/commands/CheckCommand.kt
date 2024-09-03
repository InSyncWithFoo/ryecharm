package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class CheckCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.check")
    
}
