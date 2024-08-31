package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class RemoveCommand(override val executable: Path, private val target: String) : Command(), UVCommand {
    
    override val subcommand = "remove"
    override val timeoutKey = UVTimeouts.REMOVE.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.remove")
    
    override val arguments: List<String>
        get() = listOf(target)
    
}
