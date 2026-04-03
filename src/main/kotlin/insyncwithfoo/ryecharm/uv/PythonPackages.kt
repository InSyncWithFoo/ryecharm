package insyncwithfoo.ryecharm.uv

import insyncwithfoo.ryecharm.parseAsJSONLeniently
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class PythonPackage(
    val name: String,
    val version: String,
    @SerialName("editable_project_location")
    val editableProjectLocation: String? = null
)


internal fun parsePipListOutput(raw: String) =
    raw.parseAsJSONLeniently<List<PythonPackage>>()
