package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.RuffTimeouts
import insyncwithfoo.ryecharm.message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Path


@Serializable
internal data class OptionDeprecationInfo(
    val since: String?,
    val message: String?
)


@Serializable
internal data class OptionInfo(
    val doc: String,
    val default: String,
    @SerialName("value_type")
    val valueType: String,
    val scope: String?,
    val example: String,
    val deprecated: OptionDeprecationInfo?
)


internal class ConfigCommand(override val executable: Path, private val option: String?) : Command(), RuffCommand {
    
    override val subcommand = "config"
    override val timeoutKey = RuffTimeouts.CONFIG.key
    
    override val arguments: List<String>
        get() = listOfNotNull(option, "--output-format", "json")
    
    override val runningMessage: String
        get() = message("progresses.command.ruff.config")
    
}
