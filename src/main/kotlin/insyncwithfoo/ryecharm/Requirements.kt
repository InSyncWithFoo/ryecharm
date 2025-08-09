package insyncwithfoo.ryecharm

import com.intellij.psi.PsiElement
import org.toml.lang.psi.TomlKey


private val DEPENDENCY_ARRAYS_MAP_KEYS = TOMLPath.listOf("project.optional-dependencies", "dependency-groups")

private val KNOWN_PYPROJECT_TOML_DEPENDENCY_ARRAY_KEYS = TOMLPath.listOf(
    "project.dependencies",
    "build-system.requires"
)
private val KNOWN_UV_DEPENDENCY_ARRAY_KEYS = TOMLPath.listOf(
    "constraint-dependencies",
    "dev-dependencies",
    "override-dependencies",
    "upgrade-package",
    "pip.upgrade-package"
)


private val TOMLPath.isChildOfDependencyArrayMap: Boolean
    get() = DEPENDENCY_ARRAYS_MAP_KEYS.any { this isChildOf it }


private val TOMLPath.isKnownPyprojectTomlDependencyArrayKey: Boolean
    get() {
        if (this in KNOWN_PYPROJECT_TOML_DEPENDENCY_ARRAY_KEYS) {
            return true
        }
        
        val relativized = this.relativize("tool.uv") ?: return false
        
        return relativized.isKnownUVDependencyArrayKey
    }


private val TOMLPath.isKnownUVDependencyArrayKey: Boolean
    get() = this in KNOWN_UV_DEPENDENCY_ARRAY_KEYS


/**
 * Whether the given key matches one of the following:
 *
 * * `project.dependencies`
 * * `project.optional-dependencies.*`
 * * `build-system.requires`
 * * `dependency-groups.*`
 * * `uv.constraint-dependencies`
 * * `uv.dev-dependencies`
 * * `uv.override-dependencies`
 * * `uv.upgrade-package`
 * * `uv.pip.upgrade-package`
 */
private val TomlKey.isPairedWithDependencySpecifierArray: Boolean
    get() {
        val file = containingFile.virtualFile ?: return false
        val absoluteName = this.absoluteName
        
        return when {
            file.isPyprojectToml ->
                absoluteName.isChildOfDependencyArrayMap || absoluteName.isKnownPyprojectTomlDependencyArrayKey
            
            file.isUVToml -> absoluteName.isKnownUVDependencyArrayKey
            
            else -> false
        }
    }


/**
 * @see isPairedWithDependencySpecifierArray
 */
internal val PsiElement.isDependencySpecifierString: Boolean
    get() = stringArrayTomlKey?.isPairedWithDependencySpecifierArray == true
