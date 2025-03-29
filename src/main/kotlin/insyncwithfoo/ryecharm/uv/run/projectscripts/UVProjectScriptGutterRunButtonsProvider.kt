package insyncwithfoo.ryecharm.uv.run.projectscripts

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment


/**
 * Show a line marker for each subkey
 * under the following tables:
 * 
 * * `project.scripts`
 * * `project.gui-scripts`
 *
 * Such markers can be clicked on to run
 * the corresponding script using `uv run`.
 *
 * ```toml
 *  1   | [project.scripts]
 *  2 > | foo = "lorem.ipsum:dolor"
 * ```
 */
internal class UVProjectScriptGutterRunButtonsProvider : RunLineMarkerContributor(), DumbAware {
    
    override fun getInfo(element: PsiElement): Info? {
        val segment = element.parent as? TomlKeySegment ?: return null
        val key = segment.parent as? TomlKey ?: return null
        
        if (key.projectScriptName == null) {
            return null
        }
        
        val icon = AllIcons.RunConfigurations.TestState.Run
        val actions = ExecutorAction.getActions()
        
        return Info(icon, actions)
    }
    
}
