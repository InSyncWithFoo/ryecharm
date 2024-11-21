package insyncwithfoo.ryecharm.others.dependencygroups

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.icons.PythonIcons
import insyncwithfoo.ryecharm.message
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.ext.name
import org.toml.lang.psi.TomlElementTypes.BARE_KEY
import org.toml.lang.psi.TomlTableHeader


/**
 * Show a line marker for each dependency group
 * as well as the `[dependency-groups]` table header.
 * Such markers can be clicked on to install
 * either the corresponding group or all groups.
 * 
 * ```toml
 * 1 @ | [dependency-groups]
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
        
        val tableHeaderOrKeyValuePair = key.parent
        val table = tableHeaderOrKeyValuePair.parent as? TomlTable ?: return null
        
        if (!table.isDependencyGroupsTable) {
            return null
        }
        
        return when (key.parent is TomlTableHeader) {
            true -> getInfoForTableHeader(key)
            else -> getInfoForNormalKey(key)
        }
    }
    
    private fun getInfoForTableHeader(key: TomlKey): Info {
        val icon = PythonIcons.Python.PythonPackages
        val action = InstallDependencyGroup.all(key.project)
        
        return Info(icon, arrayOf(action)) { message("lineMarkers.installDependencyGroups.all.tooltip") }
    }
    
    private fun getInfoForNormalKey(key: TomlKey): Info? {
        val groupName = key.name ?: return null
        
        val icon = PythonIcons.Python.PythonPackages
        val action = InstallDependencyGroup.group(key.project, groupName)
        
        return Info(icon, arrayOf(action)) { message("lineMarkers.installDependencyGroups.single.tooltip", groupName) }
    }
    
}
