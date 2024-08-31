package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class VersionCommand(override val executable: Path) : Command(), UVCommand {
    
    override val subcommand = "version"
    override val timeoutKey = UVTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.version")
    
}
