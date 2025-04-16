package insyncwithfoo.ryecharm.uv.documentation

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.python.requirements.RequirementsFile
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.dependencySpecifierLookAlike
import insyncwithfoo.ryecharm.host
import insyncwithfoo.ryecharm.hostFile
import insyncwithfoo.ryecharm.isDependencySpecifierString
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.stringContent
import insyncwithfoo.ryecharm.wrappingTomlLiteral
import org.toml.lang.psi.TomlFile


/**
 * Show the dependency trees for a package on hover.
 * 
 * @see isDependencySpecifierString
 */
internal class DependencyTreeDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> {
        val configurations = file.project.uvConfigurations
        
        if (!configurations.showDependencyTreesOnHover) {
            return emptyList()
        }
        
        if (file is TomlFile) {
            val element = file.findElementAt(offset) ?: return emptyList()
            
            return documentationTargets(element, file) ?: emptyList()
        }
        
        if (file is RequirementsFile) {
            val host = file.host ?: return emptyList()
            val hostFile = file.hostFile as? TomlFile ?: return emptyList()
            
            return documentationTargets(host, hostFile) ?: emptyList()
        }
        
        return emptyList()
    }
    
    private fun documentationTargets(element: PsiElement, file: TomlFile): List<DocumentationTarget>? {
        val configurations = element.project.uvConfigurations
        val virtualFile = file.virtualFile ?: return null
        
        // TODO: Any kind of requirements?
        if (!virtualFile.isPyprojectToml && !virtualFile.isUVToml) {
            return null
        }
        
        if (!element.isDependencySpecifierString) {
            return null
        }
        
        val literal = element.wrappingTomlLiteral ?: return null
        val content = literal.stringContent ?: return null
        val specifier = dependencySpecifierLookAlike.matchEntire(content) ?: return null
        val `package` = specifier.groups["name"]!!.value
        
        val targets = listOf(
            DependencyTreeDocumentationTarget(literal, `package`, inverted = false),
            DependencyTreeDocumentationTarget(literal, `package`, inverted = true)
        )
        
        return when (configurations.showInvertedDependencyTreeFirst) {
            true -> targets.reversed()
            else -> targets
        }
    }
    
}
