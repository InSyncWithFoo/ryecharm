package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class VersionCommand : Command(), RuffCommand {
    
    override val subcommand = "version"
    override val timeoutKey = RuffTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.version")
    
}
