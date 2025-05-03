package insyncwithfoo.ryecharm.ty.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class VersionCommand : Command(), TyCommand {
    
    override val subcommands = listOf("version")
    
    override val runningMessage: String
        get() = message("progresses.command.ty.version")
    
}
