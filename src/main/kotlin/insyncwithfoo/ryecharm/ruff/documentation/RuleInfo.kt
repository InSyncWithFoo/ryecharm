package insyncwithfoo.ryecharm.ruff.documentation

import insyncwithfoo.ryecharm.ruff.RuleCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType


/**
 * A string of the form `v0.1.2`.
 */
internal typealias RuffVersion = String


// https://github.com/astral-sh/ruff/blob/28aed61a22/crates/ruff_linter/src/violation.rs#L15
@Serializable
@Suppress("unused")
internal enum class FixAvailability {
    @SerialName("Sometimes") SOMETIMES,
    @SerialName("Always") ALWAYS,
    @SerialName("None") NONE;
}


// https://github.com/astral-sh/ruff/blob/28aed61a22/crates/ruff/src/commands/rule.rs#L142
@Serializable
internal data class RuleSourceLocation(
    val file: String,
    val line: Int
)


// https://github.com/astral-sh/ruff/blob/28aed61a22/crates/ruff_linter/src/codes.rs#L79
@Serializable(with = RuleStatusSerializer::class)
@Suppress("unused")
internal sealed interface RuleGroup {
    @Serializable
    data class Stable(val since: RuffVersion) : RuleGroup
    @Serializable
    data class Preview(val since: RuffVersion) : RuleGroup
    @Serializable
    data class Deprecated(val since: RuffVersion) : RuleGroup
    @Serializable
    data class Removed(val since: RuffVersion) : RuleGroup
}


internal object RuleStatusSerializer : KSerializer<RuleGroup> {
    
    override val descriptor = buildClassSerialDescriptor("RuleStatus") {
        RuleGroup::class.sealedSubclasses.forEach { `class` ->
            val name = `class`.simpleName!!
            val type = `class`.createType()
            
            element(name, serializer(type).descriptor, isOptional = true)
        }
    }
    
    override fun serialize(encoder: Encoder, value: RuleGroup) {
        throw SerializationException("The serializer must not be used")
    }
    
    override fun deserialize(decoder: Decoder): RuleGroup {
        val `object` = (decoder as JsonDecoder).decodeJsonElement().jsonObject
        val (kind, value) = `object`.entries.single()
        
        return RuleGroup::class.sealedSubclasses.firstNotNullOf { `class` ->
            val type = `class`.createType()
            @Suppress("UNCHECKED_CAST")
            val serializer = serializer(type) as KSerializer<RuleGroup>
            
            when (kind == `class`.simpleName) {
                true -> Json.decodeFromJsonElement(serializer, value)
                else -> null
            }
        }
    }
    
}


// https://github.com/astral-sh/ruff/blob/28aed61a22/crates/ruff/src/commands/rule.rs#L16
// TODO: Make fixAvailability, status and sourceLocation non-nullable once 0.14.2 is widely adopted.
@Serializable
internal data class RuleInfo(
    val name: String,
    val code: RuleCode,
    val linter: String,
    val summary: String,
    @SerialName("message_formats")
    val messageFormats: List<String>,
    val fix: String,
    @SerialName("fix_availability")
    val fixAvailability: FixAvailability? = null,
    val explanation: String?,
    val preview: Boolean,
    val status: RuleGroup? = null,
    @SerialName("source_location")
    val sourceLocation: RuleSourceLocation? = null
)
