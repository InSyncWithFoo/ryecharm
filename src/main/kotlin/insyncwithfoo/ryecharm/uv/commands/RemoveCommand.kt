package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class RemoveCommand : Command(), UVCommand {
    
    override val subcommand = "remove"
    override val timeoutKey = UVTimeouts.REMOVE.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.remove")
    
}
