package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.rye.RyeTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal typealias ProjectVersion = String


internal enum class VersionBumpType {
    MAJOR, MINOR, PATCH;
    
    override fun toString() = name.lowercase()
}


internal class VersionCommand(
    override val executable: Path,
    override val arguments: List<String>
) : Command(), RyeCommand {
    
    override val subcommand = "version"
    override val timeoutKey = RyeTimeouts.VERSION.key
    
    override val runningMessage: String
        get() = message("progresses.command.rye.version")

}
