package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import java.nio.file.Path


internal class VersionCommand(override val executable: Path) : Command(), RuffCommand {
    
    override val subcommand = "version"
    override val timeoutKey = RuffTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = super.runningMessage
    
}
