package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.rye.RyeTimeouts
import java.nio.file.Path


internal class ShowCommand(override val executable: Path) : Command(), RyeCommand {
    
    override val subcommand = "show"
    override val timeoutKey = RyeTimeouts.SHOW.key
    
}
