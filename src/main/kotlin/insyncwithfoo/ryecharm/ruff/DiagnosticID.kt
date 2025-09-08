package insyncwithfoo.ryecharm.ruff

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


// https://github.com/astral-sh/ruff/blob/2467c4352e/crates/ruff_db/src/diagnostic/mod.rs#L1047
@Serializable(with = DiagnosticIDSerializer::class)
internal sealed class DiagnosticID(val value: String) {
    /**
     * Either:
     * * `null` (which represents syntax errors in old format)
     * * Empty string (placeholder, no corresponding Rust enum member)
     * * Some kind of ID that is not understood
     */
    data object None : DiagnosticID(value = "")
    data object InvalidSyntax : DiagnosticID(value = "invalid-syntax")
    class Lint(value: RuleCode) : DiagnosticID(value)
    
    companion object
}


private class DiagnosticIDSerializer : KSerializer<DiagnosticID> {
    
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DiagnosticID", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: DiagnosticID) {
        throw SerializationException("The serializer must not be used")
    }
    
    override fun deserialize(decoder: Decoder) =
        DiagnosticID.from(decoder.decodeString())
    
}


private val KNOWN by lazy {
    DiagnosticID::class.sealedSubclasses
        .mapNotNull { it.objectInstance }
        .associateBy { it.value }
}


/**
 * Whether [this] is either [DiagnosticID.InvalidSyntax]
 * or [DiagnosticID.None].
 */
internal val DiagnosticID.isLikelySyntaxError: Boolean
    get() = when (this) {
        is DiagnosticID.None, is DiagnosticID.InvalidSyntax -> true
        is DiagnosticID.Lint -> false
    }


/**
 * The rule code associated with this [DiagnosticID.Lint], if any.
 */
internal val DiagnosticID.ruleCode: RuleCode?
    get() = when (this) {
        is DiagnosticID.None, is DiagnosticID.InvalidSyntax -> null
        is DiagnosticID.Lint -> value
    }


internal fun DiagnosticID.Companion.from(value: String?) =
    when (value) {
        null -> DiagnosticID.None
        else -> KNOWN[value] ?: DiagnosticID.Lint(value)
    }
