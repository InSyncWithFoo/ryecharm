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


internal class DependencyGroupInstaller : RunLineMarkerContributor(), DumbAware {
    
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
