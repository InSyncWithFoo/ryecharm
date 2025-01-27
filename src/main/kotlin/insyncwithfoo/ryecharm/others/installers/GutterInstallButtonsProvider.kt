package insyncwithfoo.ryecharm.others.installers

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.icons.PythonIcons
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.others.dependencygroups.isDependencyGroupsTable
import org.toml.lang.psi.TomlElementTypes.BARE_KEY
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.TomlTableHeader
import org.toml.lang.psi.ext.name


private val TomlTable.isOptionalDependenciesTable: Boolean
    get() = header.key?.absoluteName == TOMLPath("project.optional-dependencies")


private enum class TableKind {
    ProjectOptionalDependencies,
    DependencyGroups;
    
    companion object {
        fun from(table: TomlTable) = when {
            table.isDependencyGroupsTable -> DependencyGroups
            table.isOptionalDependenciesTable -> ProjectOptionalDependencies
            else -> null
        }
    }
}


/**
 * Show a line marker for each extra/dependency group
 * as well as the `[dependency-groups]`/`[project.optional-dependencies]`
 * table headers.
 *
 * Such markers can be clicked on to install
 * either the corresponding group/extra or all groups/extras.
 *
 * ```toml
 *  1 @ | [project.optional-dependencies]
 *  2 @ | lorem = [
 *  3   |     "ruff"
 *  4   | ]
 *  5   |
 *  6 @ | [dependency-groups]
 *  7 @ | foo = [
 *  8   |     "ruff"
 *  9   | ]
 * 10 @ | bar = ["a-n-plus-b"]
 * ```
 */
internal class GutterInstallButtonsProvider : RunLineMarkerContributor(), DumbAware {
    
    /**
     * Return an instance of [RunLineMarkerContributor.Info]
     * for the [BARE_KEY] leaf element within either:
     *
     * * The only [TomlKeySegment] of a group/extra key-value pair's [TomlKey], or
     * * The last such segment of a `project.optional-dependencies`/`dependency-groups` table's header.
     *
     * @see InstallDependencies
     */
    override fun getInfo(element: PsiElement): Info? {
        val segment = element.parent as? TomlKeySegment ?: return null
        val key = segment.parent as? TomlKey ?: return null
        
        if (segment != key.segments.lastOrNull()) {
            return null
        }
        
        return getInfoForKey(key)
    }
    
    private fun getInfoForKey(key: TomlKey): Info? {
        val tableHeaderOrKeyValuePair = key.parent
        val table = tableHeaderOrKeyValuePair.parent as? TomlTable ?: return null
        
        val installAll = tableHeaderOrKeyValuePair is TomlTableHeader
        val tableKind = TableKind.from(table) ?: return null
        
        val groupOrExtraName = when (installAll) {
            true -> ""
            else -> key.name ?: return null
        }
        
        val (action, tooltip) = when (Pair(tableKind, installAll)) {
            Pair(TableKind.DependencyGroups, false) -> Pair(
                InstallDependencies.group(key.project, groupOrExtraName),
                message("lineMarkers.installDependencies.group.tooltip", groupOrExtraName)
            )
            Pair(TableKind.DependencyGroups, true) -> Pair(
                InstallDependencies.allGroups(key.project),
                message("lineMarkers.installDependencies.allGroups.tooltip")
            )
            Pair(TableKind.ProjectOptionalDependencies, false) -> Pair(
                InstallDependencies.extra(key.project, groupOrExtraName),
                message("lineMarkers.installDependencies.extra.tooltip", groupOrExtraName)
            )
            Pair(TableKind.ProjectOptionalDependencies, true) -> Pair(
                InstallDependencies.allExtras(key.project),
                message("lineMarkers.installDependencies.allExtras.tooltip")
            )
            else -> return null
        }
        
        val icon = PythonIcons.Python.PythonPackages
        
        return Info(icon, arrayOf(action)) { tooltip }
    }
    
}
