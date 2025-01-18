package insyncwithfoo.ryecharm

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder


private val lenientParser = Json { ignoreUnknownKeys = true }


internal inline fun <reified T> String.parseAsJSONLeniently() =
    try {
        lenientParser.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }


internal inline fun <reified T> String.parseAsJSON() =
    try {
        Json.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }


internal inline fun <reified T> String.parseAsJSON(noinline builderAction: JsonBuilder.() -> Unit): T? {
    val json = Json(builderAction = builderAction)
    
    return try {
        json.decodeFromString<T>(this)
    } catch (_: SerializationException) {
        null
    }
}
