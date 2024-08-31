package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import java.nio.file.Path


internal class CheckCommand(
    override val executable: Path,
    private val text: String,
    private val stdinFilename: Path?
) : Command(), RuffCommand {
    
    override val subcommand = "check"
    override val timeoutKey = RuffTimeouts.CHECK.key
    
    override val stdin by ::text
    
    override val arguments: List<String>
        get() {
            val arguments = mutableListOf(
                "--no-fix", "--exit-zero", "--quiet",
                "--output-format", "json"
            )
            
            if (stdinFilename != null) {
                arguments.add("--stdin-filename")
                arguments.add(stdinFilename.toString())
            }
            
            return arguments + "-"
        }
    
}
