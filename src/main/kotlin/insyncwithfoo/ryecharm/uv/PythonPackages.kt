package insyncwithfoo.ryecharm.uv

import com.jetbrains.python.packaging.common.PythonPackage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


@Serializable
private class PythonPackageSurrogate(
    val name: String,
    val version: String,
    @SerialName("editable_project_location")
    val editableProjectLocation: String? = null
)


private fun PythonPackage(surrogate: PythonPackageSurrogate) =
    with(surrogate) {
        PythonPackage(
            name,
            version,
            isEditableMode = editableProjectLocation != null
        )
    }


internal fun parsePipListOutput(raw: String): List<PythonPackage>? {
    val json = Json { ignoreUnknownKeys = true }
    
    val parsed = try {
        json.decodeFromString<List<PythonPackageSurrogate>>(raw)
    } catch (_: SerializationException) {
        return null
    }
    
    return parsed.map { PythonPackage(it) }
}
