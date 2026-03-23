package insyncwithfoo.ryecharm.ruff.linting

import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.DiagnosticID
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
internal data class Edit(
    val content: String,
    override val location: SourceLocation,
    @SerialName("end_location")
    override val endLocation: SourceLocation
) : Ranged


@Serializable
internal data class Fix(
    val applicability: Applicability,
    val message: String?,
    val edits: List<Edit>
)


// https://github.com/astral-sh/ruff/blob/2daaf29d90/crates/ruff_db/src/diagnostic/mod.rs#L1284
@Suppress("unused")
internal enum class Severity {
    @SerialName("info") INFO,
    @SerialName("warning") WARNING,
    @SerialName("error") ERROR,
    @SerialName("fatal") FATAL;
}


// https://github.com/astral-sh/ruff/blob/2daaf29d90/crates/ruff_db/src/diagnostic/render/json.rs#L227
@Serializable
internal data class Diagnostic(
    @SerialName("code")
    val id: DiagnosticID,
    val url: String?,
    val message: String,
    val severity: Severity,
    val fix: Fix?,
    val cell: OneBasedIndex?,
    override val location: SourceLocation,
    @SerialName("end_location")
    override val endLocation: SourceLocation,
    val filename: String,
    @SerialName("noqa_row")
    val noqaRow: OneBasedIndex?
) : Ranged
