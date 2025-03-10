package insyncwithfoo.ryecharm.ruff.documentation.providers

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
import insyncwithfoo.ryecharm.others.scriptmetadata.isScriptMetadataTemporaryFile
import insyncwithfoo.ryecharm.ruff.documentation.isRuleSelector
import insyncwithfoo.ryecharm.ruff.documentation.targets.RuffRuleDocumentationTarget
import insyncwithfoo.ryecharm.stringContent
import insyncwithfoo.ryecharm.wrappingTomlLiteral
import org.toml.lang.TomlLanguage
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlLiteral


private typealias TOMLStringLiteral = TomlLiteral


/**
 * Provide documentation for rules specified in
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
            !virtualFile.run { isPyprojectToml || isRuffToml || isScriptMetadataTemporaryFile } -> return null
        }
        
        val element = file.findElementAt(offset) ?: return null
        val string = element.wrappingTomlLiteral?.takeIf { it.isString } ?: return null
        
        val array = string.parent as? TomlArray ?: return null
        val keyValuePair = array.keyValuePair ?: return null
        val key = keyValuePair.key
        
        val absoluteName = key.absoluteName
        val nameRelativeToRoot = when (virtualFile.run { isPyprojectToml || isScriptMetadataTemporaryFile }) {
            true -> absoluteName.relativize("tool.ruff") ?: return null
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
        
        val selector = stringContent!!
        
        if (!selector.isRuleSelector) {
            return null
        }
        
        return RuffRuleDocumentationTarget(this, selector)
    }
    
}
