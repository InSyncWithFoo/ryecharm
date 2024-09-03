package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class LinterCommand : Command(), RuffCommand {
    
    override val subcommand = "linter"
    override val timeoutKey = RuffTimeouts.LINTER.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.linter")
    
}
