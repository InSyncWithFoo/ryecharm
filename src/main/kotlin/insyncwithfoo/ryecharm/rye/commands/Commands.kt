package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class ConfigCommand : Command(), RyeCommand {
    
    override val subcommand = "config"
    
    override val runningMessage: String
        get() = message("progresses.command.rye.config")
    
}


internal class ShowCommand : Command(), RyeCommand {
    
    override val subcommand = "show"
    
    override val runningMessage: String
        get() = message("progresses.command.rye.show")
    
}


internal class VersionCommand : Command(), RyeCommand {
    
    override val subcommand = "version"
    
    override val runningMessage: String
        get() = message("progresses.command.rye.version")

}
