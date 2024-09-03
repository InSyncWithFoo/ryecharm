package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class FormatCommand : Command(), RuffCommand {
    
    override val subcommand = "format"
    override val timeoutKey = RuffTimeouts.FORMAT.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.format")
    
}
