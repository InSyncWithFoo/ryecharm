package insyncwithfoo.ryecharm.ruff.documentation.providers

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isRuffToml
import insyncwithfoo.ryecharm.others.scriptmetadata.isScriptMetadataTemporaryFile
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffOptionDocumentationTarget
import insyncwithfoo.ryecharm.wrappingTomlKey
import org.toml.lang.TomlLanguage
import org.toml.lang.psi.TomlKey


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-66325
/**
 * Provide documentation for keys of:
 * * The `tool.ruff` table of `pyproject.toml` and script metadata temporary files
 * * `.ruff.toml`/`ruff.toml` files
 * 
 * ```toml
 * [tool.ruff]
 * preview = true
 * # ^ hover: Whether to enable preview mode...
 * ```
 * 
 * This used to inherit from [PsiDocumentationTargetProvider].
 * The change was made so that it can be prioritized over
 * LSP4IJ's provider using `order="before ..."`,
 * which seems to require both implementations
 * being that of the same extension point.
 * 
 * Without the aforementioned change, this would only be invoked
 * when the hypothetical language server cannot handle a hover request.
 * Instead, both are invoked and shown in a paged popup,
 * with this class's popup being the first.
 * 
 * That both can coexist is important, especially when
 * the hovered element is not one this class can handle
 * but one the schema has information about
 * (e.g., a table header).
 */
internal class RuffOptionDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int) =
        listOfNotNull(documentationTarget(file, offset))
    
    private fun documentationTarget(file: PsiFile, offset: Int): DocumentationTarget? {
        val project = file.project
        val configurations = project.ruffConfigurations
        val virtualFile = file.virtualFile ?: return null
        
        when {
            !configurations.documentationPopups -> return null
            !configurations.documentationPopupsForTOMLOptions -> return null
            !file.language.isKindOf(TomlLanguage) -> return null
            !virtualFile.run { isPyprojectToml || isRuffToml || isScriptMetadataTemporaryFile } -> return null
        }
        
        val element = file.findElementAt(offset) ?: return null
        val key = element.wrappingTomlKey ?: return null
        
        val absoluteName = key.absoluteName
        val relativeName = when (virtualFile.run { isPyprojectToml || isScriptMetadataTemporaryFile }) {
            true -> absoluteName.relativize("tool.ruff") ?: return null
            else -> absoluteName
        }
        
        return key.toTarget(relativeName.toString())
    }
    
    private fun TomlKey.toTarget(option: String) =
        RuffOptionDocumentationTarget(this, option)
    
}
