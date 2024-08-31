package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import java.nio.file.Path


internal class FormatCommand(
    override val executable: Path,
    private val text: String,
    private val stdinFilename: Path?,
    private val range: OneBasedRange?
) : Command(), RuffCommand {
    
    override val subcommand = "format"
    override val timeoutKey = RuffTimeouts.FORMAT.key
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.format")
    
    override val stdin by ::text
    
    override val arguments: List<String>
        get() {
            val arguments = mutableListOf("--quiet")
            
            if (stdinFilename != null) {
                arguments.add("--stdin-filename")
                arguments.add(stdinFilename.toString())
            }
            
            if (range != null) {
                arguments.add("--range")
                arguments.add(range.toString())
            }
            
            return arguments + "-"
        }
    
}
