package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.others.scriptmetadata.isStartBlockLine
import insyncwithfoo.ryecharm.others.scriptmetadata.scriptBlock


/**
 * Show a line marker for script metadata blocks.
 * 
 * Such markers can be clicked on to run
 * the corresponding script using `uv run --script`.
 * 
 * ```python
 *  1 > | # /// script
 *  2   | # requires-python = ">=3.13"
 *  3   | # ///
 * ```
 */
internal class UVStandaloneScriptGutterRunButtonsProvider : RunLineMarkerContributor(), DumbAware {
    
    override fun getInfo(element: PsiElement): Info? {
        if (!element.isStartBlockLine) {
            return null
        }
        
        val file = element.parent as? PyFile ?: return null
        val document = file.viewProvider.document ?: return null
        
        val blockRange = scriptBlock.find(document.charsSequence)?.range ?: return null
        
        if (element != file.findElementAt(blockRange.first)) {
            return null
        }
        
        val icon = AllIcons.RunConfigurations.TestState.Run
        val actions = ExecutorAction.getActions()
        
        return Info(icon, actions)
    }
    
}
