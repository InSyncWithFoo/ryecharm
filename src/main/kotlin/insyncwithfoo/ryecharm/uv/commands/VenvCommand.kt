package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import java.nio.file.Path


internal class VenvCommand(
    override val executable: Path,
    private val venvName: String?,
    private val baseInterpreter: Path
) : Command(), UVCommand {
    
    override val subcommand = "venv"
    override val timeoutKey = UVTimeouts.VENV.key
    
    override val arguments: List<String>
        get() = listOfNotNull(venvName, "--python", baseInterpreter.toString())
    
}
