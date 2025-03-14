package insyncwithfoo.ryecharm.ruff

import com.intellij.openapi.vfs.VirtualFile
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectTomlLike
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelector
import insyncwithfoo.ryecharm.ruff.documentation.isRuleSelector
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind


internal fun TomlLiteral.extractRuleSelector(file: VirtualFile): RuleSelector? {
    val kind = this.kind as? TomlLiteralKind.String ?: return null
    
    val array = this.parent as? TomlArray ?: return null
    val keyValuePair = array.keyValuePair ?: return null
    val key = keyValuePair.key
    
    val absoluteName = key.absoluteName
    val nameRelativeToRoot = when (file.isPyprojectTomlLike) {
        true -> absoluteName.relativize("tool.ruff") ?: return null
        else -> absoluteName
    }
    
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
    
    return kind.value?.takeIf { it.isRuleSelector }
}
