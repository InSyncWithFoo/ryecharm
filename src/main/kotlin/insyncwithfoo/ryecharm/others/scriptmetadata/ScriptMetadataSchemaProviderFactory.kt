package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import insyncwithfoo.ryecharm.virtualFileManager


internal class ScriptMetadataSchemaProvider : JsonSchemaFileProvider {
    
    override fun getName() = "script-metadata"
    
    override fun getSchemaType() = SchemaType.remoteSchema
    
    /**
     * @see EditScriptMetadataFragment.asNewVirtualFile
     */
    override fun isAvailable(file: VirtualFile) =
        file.isScriptMetadataTemporaryFile
    
    override fun getSchemaFile() =
        virtualFileManager.findFileByUrl(URL)
    
    companion object {
        private const val URL = "https://json.schemastore.org/pep-723.json"
    }
    
}


internal class ScriptMetadataSchemaProviderFactory : JsonSchemaProviderFactory {
    
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> =
        listOf(ScriptMetadataSchemaProvider())
    
}
