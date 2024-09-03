package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal class SyncCommand : Command(), UVCommand {
    
    override val subcommand = "sync"
    override val timeoutKey = UVTimeouts.SYNC.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.sync")
    
}
