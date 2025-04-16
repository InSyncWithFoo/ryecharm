package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class PipListCommand : Command(), UVCommand {
    
    override val subcommand = "pip"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.piplist")
    
}


internal class PipTreeCommand : Command(), UVCommand {
    
    override val subcommand = "pip"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.piptree")
    
}
