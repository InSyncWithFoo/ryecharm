package insyncwithfoo.ryecharm.ruff.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.startOffset
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.mayContainRuffOptions
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import insyncwithfoo.ryecharm.ruff.documentation.getRuleNameToCodeMap
import insyncwithfoo.ryecharm.ruff.extractRuleSelector
import kotlinx.coroutines.CoroutineScope
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind


private const val CONTINUE_PROCESSING = true


private val RULE_NAME = Key.create<RuleName>("${RyeCharm.ID}.ruff.ruleName")


private operator fun Int.plus(textRange: TextRange) =
    TextRange(this + textRange.startOffset, this + textRange.endOffset)


/**
 * Fold a single-rule selector (e.g., `RUF001`)
 * into that rule's name.
 */
internal class RuffRuleSelectorFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    
    override fun isRegionCollapsedByDefault(node: ASTNode) =
        node.psi?.project?.ruffConfigurations?.foldSingleRuleSelectorsByDefault ?: true
    
    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val element = node.psi as? TomlLiteral ?: throw RuntimeException()
        val name = element.getUserData(RULE_NAME) ?: throw RuntimeException()
        
        return name
    }
    
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        when {
            root !is TomlFile -> return
            !root.viewProvider.virtualFile.mayContainRuffOptions -> return
            quick -> return
        }
        
        val codeToNameMap = root.project.getCodeToNameMapOrTriggerRetrieving() ?: return
        val virtualFile = root.containingFile.viewProvider.virtualFile
        
        PsiTreeUtil.processElements(root, TomlLiteral::class.java) { element ->
            element.getDescriptor(virtualFile, codeToNameMap)?.let {
                descriptors += it
            }
            CONTINUE_PROCESSING
        }
    }
    
    private fun Project.getCodeToNameMapOrTriggerRetrieving(): Map<RuleCode, RuleName>? {
        val cache = RuffCache.getInstance(this)
        val nameToCodeMap = cache.ruleNameToCodeMap?.result
        
        if (nameToCodeMap == null) {
            launch<Coroutine> { getRuleNameToCodeMap() }
            return null
        }
        
        return nameToCodeMap.entries.associate { it.value to it.key }
    }
    
    private fun TomlLiteral.getDescriptor(
        file: VirtualFile,
        codeToNameMap: Map<RuleCode, RuleName>
    ): FoldingDescriptor? {
        val kind = kind as? TomlLiteralKind.String ?: return null
        val valueRelativeRange = kind.offsets.value ?: return null
        val valueAbsoluteRange = startOffset + valueRelativeRange
        
        val code = this.extractRuleSelector(file) ?: return null
        val name = codeToNameMap[code] ?: return null
        
        putUserData(RULE_NAME, name)
        
        return FoldingDescriptor(this, valueAbsoluteRange)
    }
    
    @Service(Service.Level.PROJECT)
    internal class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
