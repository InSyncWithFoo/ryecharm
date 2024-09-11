package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class CheckCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.check")
    
}


internal class CleanCommand : Command(), RuffCommand {
    
    override val subcommand = "clean"
    override val timeoutKey = RuffTimeouts.CLEAN.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.clean")
    
}


internal class ConfigCommand : Command(), RuffCommand {
    
    override val subcommand = "config"
    override val timeoutKey = RuffTimeouts.CONFIG.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.config")
    
}


internal class OptimizeImportsCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key  // FIXME: Or FORMAT/specialized?
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.optimizeImports")
    
}


internal class FixAllCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key  // FIXME: Or specialized?
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.fixAll")
    
}


internal class LinterCommand : Command(), RuffCommand {
    
    override val subcommand = "linter"
    override val timeoutKey = RuffTimeouts.LINTER.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.linter")
    
}


internal class FormatCommand : Command(), RuffCommand {
    
    override val subcommand = "format"
    override val timeoutKey = RuffTimeouts.FORMAT.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.format")
    
}


internal class RuleCommand : Command(), RuffCommand {
    
    override val subcommand = "rule"
    override val timeoutKey = RuffTimeouts.RULE.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.rule")
    
}


internal class VersionCommand : Command(), RuffCommand {
    
    override val subcommand = "version"
    override val timeoutKey = RuffTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.version")
    
}
