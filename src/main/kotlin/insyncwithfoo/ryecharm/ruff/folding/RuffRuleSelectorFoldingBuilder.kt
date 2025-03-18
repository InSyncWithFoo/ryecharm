package insyncwithfoo.ryecharm.ruff.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.mayContainRuffOptions
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import insyncwithfoo.ryecharm.ruff.extractRuleSelector
import insyncwithfoo.ryecharm.ruff.plus
import insyncwithfoo.ryecharm.traverse
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind


private val RULE_NAME = Key.create<RuleName>("${RyeCharm.ID}.ruff.folding.ruleName")


/**
 * Fold a single-rule selector (e.g., `RUF001`)
 * into that rule's name.
 * 
 * Expanded:
 * 
 * ```toml
 * [tool.ruff.lint]
 * select = ["Q000"]
 * ```
 * 
 * Folded:
 * 
 * ```toml
 * [tool.ruff.lint]
 * select = ["bad-quotes-inline-string"]
 * ```
 */
internal class RuffRuleSelectorFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    
    override fun isRegionCollapsedByDefault(node: ASTNode) =
        node.psi?.project?.ruffConfigurations?.foldSingleRuleSelectorsByDefault ?: true
    
    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val element = node.psi as? TomlLiteral ?: throw CannotFold()
        val name = element.getUserData(RULE_NAME) ?: throw CannotFold()
        
        return name
    }
    
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        val virtualFile = root.containingFile.viewProvider.virtualFile
        
        when {
            root !is TomlFile -> return
            !virtualFile.mayContainRuffOptions -> return
        }
        
        val codeToNameMap = root.project.getCodeToNameMapOrTriggerRetrieving() ?: return
        
        root.traverse<TomlLiteral> { element ->
            element.getDescriptor(virtualFile, codeToNameMap)?.let {
                descriptors += it
            }
        }
    }
    
    private fun TomlLiteral.getDescriptor(
        file: VirtualFile,
        codeToNameMap: Map<RuleCode, RuleName>
    ): FoldingDescriptor? {
        val kind = kind as? TomlLiteralKind.String ?: return null
        val valueRelativeRange = kind.offsets.value ?: return null
        val valueAbsoluteRange = valueRelativeRange + startOffset
        
        val code = this.extractRuleSelector(file) ?: return null
        val name = codeToNameMap[code] ?: return null
        
        putUserData(RULE_NAME, name)
        
        return FoldingDescriptor(this, valueAbsoluteRange)
    }
    
}
