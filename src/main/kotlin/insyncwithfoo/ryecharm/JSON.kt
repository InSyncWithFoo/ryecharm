package insyncwithfoo.ryecharm

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder


private val lenientParser = Json { ignoreUnknownKeys = true }
private val prettyPrinter = Json { prettyPrint = true }


/**
 * Parse the given string as JSON, ignoring unknown keys and returning `null` on failure.
 */
internal inline fun <reified T : Any> String.parseAsJSONLeniently() =
    try {
        lenientParser.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }


/**
 * Parse the given string as JSON, returning `null` on failure.
 */
internal inline fun <reified T : Any> String.parseAsJSON() =
    try {
        Json.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }


/**
 * Parse the given string as JSON using the given serializer,
 * returning `null` on failure.
 */
internal inline fun <reified T : Any> String.parseAsJSON(serializer: KSerializer<T>) =
    try {
        Json.decodeFromString(serializer, this)
    } catch (_: SerializationException) {
        null
    }


/**
 * Parse the given string as JSON
 * using the builder built with [builderAction],
 * returning `null` on failure.
 */
internal inline fun <reified T : Any> String.parseAsJSON(noinline builderAction: JsonBuilder.() -> Unit): T? {
    val json = Json(builderAction = builderAction)
    
    return try {
        json.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }
}


/**
 * Convert the given object to JSON.
 */
internal inline fun <reified T : Any> T.stringifyToJSON() =
    Json.encodeToString<T>(this)


/**
 * Convert the given object to JSON,
 * pretty printed (4 spaces indentation).
 */
internal inline fun <reified T : Any> T.stringifyToPrettyJSON() =
    prettyPrinter.encodeToString<T>(this)
