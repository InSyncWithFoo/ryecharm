package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class ConfigCommand : Command(), RuffCommand {
    
    override val subcommand = "config"
    override val timeoutKey = RuffTimeouts.CONFIG.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.config")
    
}
