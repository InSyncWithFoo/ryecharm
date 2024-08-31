package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.rye.RyeTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class ConfigCommand(override val executable: Path) : Command(), RyeCommand {
    
    override val subcommand = "config"
    override val timeoutKey = RyeTimeouts.CONFIG.key
    
    override val runningMessage: String
        get() = message("progresses.command.rye.config")
    
    override val arguments: List<String>
        get() = listOf("--show-path")
    
}
