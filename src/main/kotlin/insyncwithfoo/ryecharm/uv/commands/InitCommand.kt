package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class InitCommand : Command(), UVCommand {
    
    override val subcommand = "init"
    override val timeoutKey = UVTimeouts.INIT.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.init")
    
}
