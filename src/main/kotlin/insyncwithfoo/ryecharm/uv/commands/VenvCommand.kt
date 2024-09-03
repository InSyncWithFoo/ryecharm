package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class VenvCommand : Command(), UVCommand {
    
    override val subcommand = "venv"
    override val timeoutKey = UVTimeouts.VENV.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.venv")
    
}
