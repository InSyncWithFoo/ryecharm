package insyncwithfoo.ryecharm.uv

import com.jetbrains.python.packaging.common.PythonPackage
import insyncwithfoo.ryecharm.parseAsJSONLeniently
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
private data class PythonPackageSurrogate(
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


internal fun parsePipListOutput(raw: String) =
    raw.parseAsJSONLeniently<List<PythonPackageSurrogate>>()?.map {
        PythonPackage(it)
    }
