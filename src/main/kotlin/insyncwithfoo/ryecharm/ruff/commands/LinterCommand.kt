package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import kotlinx.serialization.Serializable
import java.nio.file.Path


@Serializable
internal data class Category(
    val prefix: String,
    val name: String
)


@Serializable
internal data class Linter(
    val prefix: String,
    val name: String,
    val categories: List<Category>? = null
)


internal class LinterCommand(override val executable: Path) : Command(), RuffCommand {
    
    override val subcommand = "linter"
    override val timeoutKey = RuffTimeouts.LINTER.key
    
    override val arguments: List<String>
        get() = listOf("--output-format", "json")
    
}
