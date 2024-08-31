package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class CleanCommand(override val executable: Path) : Command(), RuffCommand {
    
    override val subcommand = "clean"
    override val timeoutKey = RuffTimeouts.CLEAN.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.clean")
    
}
