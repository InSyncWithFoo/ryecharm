package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.message


internal class AnalyzeGraphCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("analyze", "graph")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.analyzeGraph")
    
}


internal class CheckCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("check")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.check")
    
}


internal class CleanCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("clean")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.clean")
    
}


internal class ConfigCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("config")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.config")
    
}


internal class FormatCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("format")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.format")
    
}


internal class LinterCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("linter")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.linter")
    
}


internal class RuleCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("rule")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.rule")
    
}


internal class VersionCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("version")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.version")
    
}


internal class FixCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("check")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.fix")
    
}


internal class OptimizeImportsCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("check")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.optimizeImports")
    
}


internal class OrganizeImportsCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("check")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.organizeImports")
    
}


internal class ShowSettingsCommand : Command(), RuffCommand {
    
    override val subcommands = listOf("check")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.showSettings")
    
}
