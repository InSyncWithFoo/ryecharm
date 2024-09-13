package insyncwithfoo.ryecharm.ruff.documentation.toml

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.PsiDocumentationTargetProvider
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isRuffToml
import insyncwithfoo.ryecharm.wrappingTomlKey
import org.toml.lang.TomlLanguage
import org.toml.lang.psi.TomlKey


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
        
        val tomlKey = element.wrappingTomlKey ?: return emptyList()
        val target = when {
            virtualFile.isPyprojectToml -> tomlKey.pyprojectTomlDocumentationTarget()
            else -> tomlKey.ruffTomlDocumentationTarget(virtualFile.name)
        }
        
        return listOfNotNull(target)
    }
    
    private fun TomlKey.pyprojectTomlDocumentationTarget(): RuffOptionDocumentationTarget? {
        val relativeName = absoluteName.relativize(TOMLPath("tool.ruff")) ?: return null
        
        return RuffOptionDocumentationTarget(this, relativeName.toString(), "pyproject.toml")
    }
    
    private fun TomlKey.ruffTomlDocumentationTarget(fileName: String) =
        RuffOptionDocumentationTarget(this, absoluteName.toString(), fileName)
    
}
