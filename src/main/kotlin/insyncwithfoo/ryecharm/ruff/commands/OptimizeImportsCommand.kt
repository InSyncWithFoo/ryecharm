package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message


internal class OptimizeImportsCommand : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key  // FIXME: Or FORMAT/specialized?
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.optimizeImports")
    
}
