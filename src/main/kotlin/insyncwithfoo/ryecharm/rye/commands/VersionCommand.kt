package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.rye.RyeTimeouts
import insyncwithfoo.ryecharm.message


internal class VersionCommand : Command(), RyeCommand {
    
    override val subcommand = "version"
    override val timeoutKey = RyeTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = message("progresses.command.rye.version")

}
