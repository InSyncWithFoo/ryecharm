package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.rye.RyeTimeouts
import insyncwithfoo.ryecharm.message


internal class ShowCommand : Command(), RyeCommand {
    
    override val subcommand = "show"
    override val timeoutKey = RyeTimeouts.SHOW.key
    
    override val runningMessage: String
        get() = message("configurations.timeouts.rye.show")
    
}
