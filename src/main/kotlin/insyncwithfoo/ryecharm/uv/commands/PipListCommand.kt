package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import java.nio.file.Path


internal class PipListCommand(override val executable: Path) : Command(), UVCommand {
    
    override val subcommand = "pip"
    override val timeoutKey = UVTimeouts.PIP_LIST.key
    
    override val arguments: List<String>
        get() = listOf("list", "--format", "json")
    
}
