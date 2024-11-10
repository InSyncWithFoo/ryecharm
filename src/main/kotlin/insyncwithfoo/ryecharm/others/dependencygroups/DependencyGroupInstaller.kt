package insyncwithfoo.ryecharm.others.dependencygroups

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.icons.PythonIcons
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.message
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.ext.name
import org.toml.lang.psi.TomlElementTypes.BARE_KEY


/**
 * Show a line marker for each dependency group.
 * Such markers can be clicked on to install the corresponding group.
 * 
 * ```toml
 * 1   | [dependency-groups]
 * 2 @ | foo = [
 * 3   |     "ruff"
 * 4   | ]
 * 5 @ | bar = ["a-n-plus-b"]
 * ```
 */
internal class DependencyGroupInstaller : RunLineMarkerContributor(), DumbAware {
    
    /**
     * Return an instance of [RunLineMarkerContributor.Info]
     * for the [BARE_KEY] leaf element within
     * the only [TomlKeySegment] of a group's [TomlKey].
     * 
     * @see InstallDependencyGroup
     */
    override fun getInfo(element: PsiElement): Info? {
        val segment = element.parent as? TomlKeySegment ?: return null
        val key = segment.parent as? TomlKey ?: return null
        val table = key.keyValuePair?.parent as? TomlTable ?: return null
        
        if (!table.isDependencyGroupsTable) {
            return null
        }
        
        val groupName = key.name ?: return null
        
        val icon = PythonIcons.Python.PythonPackages
        val action = InstallDependencyGroup(element.project, groupName)
        
        return Info(icon, arrayOf(action)) { message("lineMarkers.installDependencyGroup.tooltip", groupName) }
    }
    
}
