package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import com.jetbrains.python.psi.PyFile


/**
 * Fold a script metadata block into `# /// script`.
 */
internal class ScriptMetadataFoldingBuilder : CustomFoldingBuilder(), DumbAware {
    
    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange) =
        "# /// script"
    
    override fun isRegionCollapsedByDefault(node: ASTNode) = false
    
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is PyFile) {
            return
        }
        
        val block = scriptBlock.find(document.charsSequence) ?: return
        val blockRange = TextRange(block.range.first, block.range.last + 1)
        
        for (element in root.children) {
            if (element !is PsiComment) {
                continue
            }
            
            if (element.startOffset == blockRange.startOffset) {
                descriptors += FoldingDescriptor(element, blockRange)
                break
            }
        }
    }
    
}
