package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class CheckCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.check")
    
}


internal class CleanCommand : Command(), RuffCommand {
    
    override val subcommand = "clean"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.clean")
    
}


internal class ConfigCommand : Command(), RuffCommand {
    
    override val subcommand = "config"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.config")
    
}


internal class OptimizeImportsCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.optimizeImports")
    
}


internal class FixAllCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.fixAll")
    
}


internal class OrganizeImportsCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.organizeImports")
    
}


internal class LinterCommand : Command(), RuffCommand {
    
    override val subcommand = "linter"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.linter")
    
}


internal class FormatCommand : Command(), RuffCommand {
    
    override val subcommand = "format"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.format")
    
}


internal class RuleCommand : Command(), RuffCommand {
    
    override val subcommand = "rule"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.rule")
    
}


internal class VersionCommand : Command(), RuffCommand {
    
    override val subcommand = "version"
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.version")
    
}
