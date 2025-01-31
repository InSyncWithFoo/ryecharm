package insyncwithfoo.ryecharm.ruff.documentation

import insyncwithfoo.ryecharm.ruff.RuleCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class RuleInfo(
    val name: String,
    val code: RuleCode,
    val linter: String,
    val summary: String,
    @SerialName("message_formats")
    val messageFormats: List<String>,
    val fix: String,
    val explanation: String?,
    val preview: Boolean
)
