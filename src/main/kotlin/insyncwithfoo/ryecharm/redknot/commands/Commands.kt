package insyncwithfoo.ryecharm.redknot.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class VersionCommand : Command(), RedKnotCommand {
    
    override val subcommand = "version"
    
    override val runningMessage: String
        get() = message("progresses.command.redknot.version")
    
}
