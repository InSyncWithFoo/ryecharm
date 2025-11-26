package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class AddCommand : Command(), UVCommand {
    
    override val subcommands = listOf("add")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.add")
    
}


internal class InitCommand : Command(), UVCommand {
    
    override val subcommands = listOf("init")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.init")
    
}


internal class InstallDependenciesCommand(private val kind: String) : Command(), UVCommand {
    
    @Deprecated("This constructor must not be used", level = DeprecationLevel.HIDDEN)
    @Suppress("unused")
    constructor() : this("")
    
    override val subcommands = listOf("sync")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.installDependencies", kind)
    
}


internal class RemoveCommand : Command(), UVCommand {
    
    override val subcommands = listOf("remove")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.remove")
    
}


internal class SyncCommand : Command(), UVCommand {
    
    override val subcommands = listOf("sync")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.sync")
    
}


internal class UpgradeCommand : Command(), UVCommand {
    
    override val subcommands = listOf("add")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.upgrade")
    
}


internal class VenvCommand : Command(), UVCommand {
    
    override val subcommands = listOf("venv")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.venv")
    
}


internal class VersionCommand : Command(), UVCommand {
    
    override val subcommands = listOf("version")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.version")
    
}


internal class PipCompileCommand : Command(), UVCommand {
    
    override val subcommands = listOf("pip", "compile")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.pipCompile")
    
}


internal class PipListCommand : Command(), UVCommand {
    
    override val subcommands = listOf("pip", "list")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.pipList")
    
}


internal class PipTreeCommand : Command(), UVCommand {
    
    override val subcommands = listOf("pip", "tree")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.pipTree")
    
}


internal class SelfUpdateCommand : Command(), UVCommand {
    
    override val subcommands = listOf("self", "update")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.selfUpdate")
    
}


internal class SelfVersionCommand : Command(), UVCommand {
    
    override val subcommands = listOf("self", "version")
    
    override val runningMessage: String
        get() = message("progresses.command.uv.selfVersion")
    
}
