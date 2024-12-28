package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class InitCommand : Command(), UVCommand {
    
    override val subcommand = "init"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.init")
    
}


internal class AddCommand : Command(), UVCommand {
    
    override val subcommand = "add"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.add")
    
}


internal class RemoveCommand : Command(), UVCommand {
    
    override val subcommand = "remove"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.remove")
    
}


internal class UpgradeCommand : Command(), UVCommand {
    
    override val subcommand = "add"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.upgrade")
    
}


internal class SyncCommand : Command(), UVCommand {
    
    override val subcommand = "sync"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.sync")
    
}


internal class InstallDependenciesCommand(private val kind: String) : Command(), UVCommand {
    
    override val subcommand = "sync"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.installDependencies", kind)
    
}


internal class VenvCommand : Command(), UVCommand {
    
    override val subcommand = "venv"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.venv")
    
}


internal class VersionCommand : Command(), UVCommand {
    
    override val subcommand = "version"
    
    override val runningMessage: String
        get() = message("progresses.command.uv.version")
    
}
