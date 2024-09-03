package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class AddCommand : Command(), UVCommand {
    
    override val subcommand = "add"
    override val timeoutKey = UVTimeouts.ADD.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.add")
    
}
