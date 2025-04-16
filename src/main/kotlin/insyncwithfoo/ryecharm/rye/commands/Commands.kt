package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class ConfigCommand : Command(), RyeCommand {
    
    override val subcommands = listOf("config")
    
    override val runningMessage: String
        get() = message("progresses.command.rye.config")
    
}


internal class ShowCommand : Command(), RyeCommand {
    
    override val subcommands = listOf("show")
    
    override val runningMessage: String
        get() = message("progresses.command.rye.show")
    
}


internal class VersionCommand : Command(), RyeCommand {
    
    override val subcommands = listOf("version")
    
    override val runningMessage: String
        get() = message("progresses.command.rye.version")

}
