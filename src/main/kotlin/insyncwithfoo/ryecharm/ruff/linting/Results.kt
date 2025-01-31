package insyncwithfoo.ryecharm.ruff.linting

import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.OneBasedIndex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


internal interface Ranged {
    val location: SourceLocation
    val endLocation: SourceLocation
}


@Suppress("unused")
internal enum class Applicability(private val label: String) {
    @SerialName("displayonly") DISPLAY_ONLY(message("intentions.ruff.fixViolation.applicability.displayOnly")),
    @SerialName("unsafe") UNSAFE(message("intentions.ruff.fixViolation.applicability.unsafe")),
    @SerialName("safe") SAFE(message("intentions.ruff.fixViolation.applicability.safe"));
    
    override fun toString() = label
}


@Serializable
internal data class SourceLocation(
    val row: OneBasedIndex,
    val column: OneBasedIndex
)


@Serializable
internal data class ExpandedEdit(
    val content: String,
    override val location: SourceLocation,
    @SerialName("end_location")
    override val endLocation: SourceLocation
) : Ranged


@Serializable
internal data class Fix(
    val applicability: Applicability,
    val message: String?,
    val edits: List<ExpandedEdit>
)


@Serializable
internal data class Diagnostic(
    val code: String?,
    val url: String?,
    val message: String,
    val fix: Fix?,
    val cell: OneBasedIndex?,
    override val location: SourceLocation,
    @SerialName("end_location")
    override val endLocation: SourceLocation,
    val filename: String,
    @SerialName("noqa_row")
    val noqaRow: OneBasedIndex?
) : Ranged
