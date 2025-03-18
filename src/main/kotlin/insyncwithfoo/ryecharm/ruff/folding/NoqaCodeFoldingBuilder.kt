package insyncwithfoo.ryecharm.ruff.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.ruff.NoqaComment
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.asTextRange
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import insyncwithfoo.ryecharm.ruff.minus
import insyncwithfoo.ryecharm.traverse


private val RULE_CODES_TO_NAMES = Key.create<Map<RuleCode, RuleName>>("${RyeCharm.ID}.ruff.folding.ruleCodesToNames")


/**
 * Fold rule codes in `# noqa` comments into that rule's name.
 *
 * Expanded:
 *
 * ```python
 * # noqa: Q000
 * ```
 *
 * Folded:
 *
 * ```python
 * # noqa: bad-quotes-inline-string
 * ```
 */
internal class NoqaCodeFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    
    override fun isRegionCollapsedByDefault(node: ASTNode) =
        node.psi?.project?.ruffConfigurations?.foldNoqaCodesByDefault ?: true
    
    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val element = node.psi as? PsiComment ?: throw CannotFold()
        val codesToNames = element.getUserData(RULE_CODES_TO_NAMES) ?: throw CannotFold()
        
        val relativeRange = range - element.startOffset
        val code = relativeRange.substring(node.text)
        val name = codesToNames[code] ?: throw CannotFold()
        
        return name
    }
    
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is PyFile) {
            return
        }
        
        val codeToNameMap = root.project.getCodeToNameMapOrTriggerRetrieving() ?: return
        
        root.traverse<PsiComment> { element ->
            val noqaComment = NoqaComment.parse(element) ?: return@traverse
            
            val suppressedCodesAndNames = noqaComment.codesAndRanges.mapNotNull { (code, range) ->
                codeToNameMap[code]?.let { name ->
                    descriptors += FoldingDescriptor(element, range.asTextRange())
                    code to name
                }
            }
            
            element.putUserData(RULE_CODES_TO_NAMES, suppressedCodesAndNames.toMap())
        }
        
    }
    
}
