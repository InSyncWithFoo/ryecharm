package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class OptimizeImportsCommand(
    override val executable: Path,
    private val text: String,
    private val stdinFilename: Path?
) : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key  // FIXME: Or FORMAT/specialized?
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.optimizeImports")
    
    override val stdin by ::text
    
    override val arguments: List<String>
        get() {
            val arguments = mutableListOf(
                "--fix", "--exit-zero", "--quiet",
                "--select", "I",
            )
            
            if (stdinFilename != null) {
                arguments.add("--stdin-filename")
                arguments.add(stdinFilename.toString())
            }
            
            return arguments + "-"
        }
    
}
