package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class InitCommand(override val executable: Path) : Command(), UVCommand {
    
    override val subcommand = "init"
    override val timeoutKey = UVTimeouts.INIT.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.init")
    
}
