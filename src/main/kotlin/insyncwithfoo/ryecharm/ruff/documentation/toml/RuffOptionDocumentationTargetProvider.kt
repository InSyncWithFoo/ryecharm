package insyncwithfoo.ryecharm.ruff.documentation.toml

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isRuffToml
import org.toml.lang.TomlLanguage
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlTable


private val TomlTable.absoluteName: String
    get() = header.key?.text.orEmpty()


private val TomlKey.name: String
    get() = segments.joinToString(".") { it.text }


private val TomlKey.keyValuePair: TomlKeyValue?
    get() = parent as? TomlKeyValue


private val TomlKey.table: TomlTable?
    get() = keyValuePair?.parent as? TomlTable


private val TomlKey.absoluteName: String
    get() = when {
        parent === containingFile -> name
        table == null -> name
        else -> "${table!!.absoluteName}.$name"
    }


/**
 * Return either:
 * 
 * * Itself (`this` is a [TomlKey])
 * * Its parent (`this` is possibly a [TomlKeySegment])
 * * Its grandparent (`this` is possibly a [PsiElement]`(BARE_KEY)`)
 * * `null`, when none of the above succeeds.
 */
private val PsiElement.wrappingTomlKey: TomlKey?
    get() = this as? TomlKey
        ?: parent as? TomlKey
        ?: parent.parent as? TomlKey


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-66325
/**
 * Provide documentation for keys of:
 * * The `tool.ruff` table of `pyproject.toml`
 * * `.ruff.toml`/`ruff.toml` files
 * 
 * ```toml
 * [tool.ruff]
 * preview = true
 * # ^ hover: Whether to enable preview mode...
 * ```
 */
internal class RuffOptionDocumentationTargetProvider : PsiDocumentationTargetProvider {
    
    override fun documentationTargets(element: PsiElement, originalElement: PsiElement?): List<DocumentationTarget> {
        val project = element.project
        val configurations = project.ruffConfigurations
        val file = element.containingFile ?: return emptyList()
        val virtualFile = file.virtualFile ?: return emptyList()
        
        when {
            !configurations.documentationPopups -> return emptyList()
            !configurations.documentationPopupsForTOMLOptions -> return emptyList()
            !file.language.isKindOf(TomlLanguage) -> return emptyList()
            !virtualFile.isPyprojectToml && !virtualFile.isRuffToml -> return emptyList()
        }
        
        val tomlKey = element.wrappingTomlKey
        val target = when {
            virtualFile.isPyprojectToml -> tomlKey?.pyprojectTomlDocumentationTarget()
            else -> tomlKey?.ruffTomlDocumentationTarget(virtualFile.name)
        }
        
        return listOfNotNull(target)
    }
    
    private fun TomlKey.pyprojectTomlDocumentationTarget(): RuffOptionDocumentationTarget? {
        val topLevelTableName = "tool.ruff"
        val absoluteName = this.absoluteName
        
        if (!absoluteName.startsWith(topLevelTableName)) {
            return null
        }
        
        val relativeName = absoluteName.removePrefix("$topLevelTableName.")
        
        return RuffOptionDocumentationTarget(this, relativeName, "pyproject.toml")
    }
    
    private fun TomlKey.ruffTomlDocumentationTarget(fileName: String) =
        RuffOptionDocumentationTarget(this, absoluteName, fileName)
    
}
