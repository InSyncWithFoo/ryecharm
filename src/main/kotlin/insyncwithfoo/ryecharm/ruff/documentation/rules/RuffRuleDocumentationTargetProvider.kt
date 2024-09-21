package insyncwithfoo.ryecharm.ruff.documentation.rules

import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isRuffToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.wrappingLiteral
import org.toml.lang.TomlLanguage
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlElementTypes
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.elementType


private typealias TOMLStringLiteral = TomlLiteral


private fun TOMLStringLiteral.stripQuotes(): String {
    val quoteLength = when (children.single().elementType) {
        TomlElementTypes.BASIC_STRING, TomlElementTypes.LITERAL_STRING -> 1
        TomlElementTypes.MULTILINE_BASIC_STRING, TomlElementTypes.MULTILINE_LITERAL_STRING -> 3
        else -> throw RuntimeException()
    }
    
    return text.drop(quoteLength).dropLast(quoteLength)
}


/**
 * Provide documentation for rules defined in
 * the following selector arrays:
 * 
 * * (`lint`) `extend-fixable`
 * * (`lint`) `extend-ignore` (deprecated)
 * * (`lint`) `extend-per-file-ignores.*`
 * * (`lint`) `extend-safe-fixes`
 * * (`lint`) `extend-select`
 * * (`lint`) `extend-unfixable`
 * * (`lint`) `extend-unsafe-fixes`
 * * (`lint`) `fixable`
 * * (`lint`) `ignore`
 * * (`lint`) `per-file-ignores.*`
 * * (`lint`) `select`
 * * (`lint`) `unfixable`
 * 
 * The top-level settings as well as `extend-ignore`
 * are deprecated and should be removed
 * once they are no longer recognized by Ruff itself.
 */
internal class RuffRuleDocumentationTargetProvider : DocumentationTargetProvider {
    
    override fun documentationTargets(file: PsiFile, offset: Int) =
        listOfNotNull(documentationTarget(file, offset))
    
    private fun documentationTarget(file: PsiFile, offset: Int): DocumentationTarget? {
        val project = file.project
        val configurations = project.ruffConfigurations
        val virtualFile = file.virtualFile ?: return null
        
        when {
            !configurations.documentationPopups -> return null
            !configurations.documentationPopupsForTOMLRuleCodes -> return null
            !file.language.isKindOf(TomlLanguage) -> return null
            !virtualFile.isPyprojectToml && !virtualFile.isRuffToml -> return null
        }
        
        val element = file.findElementAt(offset) ?: return null
        val string = element.wrappingLiteral?.takeIf { it.isString } ?: return null
        
        val array = string.parent as? TomlArray ?: return null
        val keyValuePair = array.keyValuePair ?: return null
        val key = keyValuePair.key
        
        val absoluteName = key.absoluteName
        val nameRelativeToRoot = when {
            virtualFile.isPyprojectToml -> absoluteName.relativize("tool.ruff") ?: return null
            else -> absoluteName
        }
        
        return string.toTarget(nameRelativeToRoot)
    }
    
    private fun TOMLStringLiteral.toTarget(nameRelativeToRoot: TOMLPath): DocumentationTarget? {
        val nameRelativeToLint = nameRelativeToRoot.relativize("lint") ?: nameRelativeToRoot
        
        val recognizedArrays = TOMLPath.listOf(
            "fixable", "extend-fixable",
            "ignore", "extend-ignore",
            "select", "extend-select",
            "unfixable", "extend-unfixable",
            "extend-safe-fixes", "extend-unsafe-fixes"
        )
        val recognizedMaps = TOMLPath.listOf(
            "per-file-ignores",
            "extend-per-file-ignores"
        )
        
        val notRecognizedArray = nameRelativeToLint !in recognizedArrays
        val notRecognizedMap = recognizedMaps.none { nameRelativeToLint isChildOf it }
        
        if (notRecognizedArray && notRecognizedMap) {
            return null
        }
        
        val content = this.stripQuotes()
        val fileName = containingFile.virtualFile!!.name
        
        return RuffRuleDocumentationTarget(this, content, fileName)
    }
    
}
