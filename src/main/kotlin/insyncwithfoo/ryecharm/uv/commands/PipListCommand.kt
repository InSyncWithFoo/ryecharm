package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class PipListCommand : Command(), UVCommand {
    
    override val subcommand = "pip"
    override val timeoutKey = UVTimeouts.PIP_LIST.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.piplist")
    
}
