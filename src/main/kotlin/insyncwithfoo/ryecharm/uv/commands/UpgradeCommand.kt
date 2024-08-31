package insyncwithfoo.ryecharm.uv.commands

import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal class UpgradeCommand(
    override val executable: Path,
    private val target: PythonPackageSpecification
) : Command(), UVCommand {
    
    override val subcommand = "add"
    override val timeoutKey = UVTimeouts.ADD.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.upgrade")
    
    override val arguments: List<String>
        get() = listOf(target.toPEP508Format(), "--upgrade")
    
}
