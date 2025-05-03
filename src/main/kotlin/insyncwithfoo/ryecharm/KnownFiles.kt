package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile


/**
 * Whether the given file is `.rye/config.toml`.
 */
internal val VirtualFile.isRyeConfigToml: Boolean
    get() = parent?.name == ".rye" && name == "config.toml"


/**
 * Whether the given file is `pyproject.toml`.
 */
internal val VirtualFile.isPyprojectToml: Boolean
    get() = name == "pyproject.toml"


/**
 * Whether the given file is `uv.lock`.
 */
internal val VirtualFile.isUVLock: Boolean
    get() = name == "uv.lock"


/**
 * Whether the given file is `uv.toml`.
 */
internal val VirtualFile.isUVToml: Boolean
    get() = name == "uv.toml"


/**
 * Whether the given file is `ruff.toml` or `.ruff.toml`.
 */
internal val VirtualFile.isRuffToml: Boolean
    get() = name == "ruff.toml" || name == ".ruff.toml"


/**
 * Whether the given file is `.python-version`.
 */
internal val VirtualFile.isPythonVersion: Boolean
    get() = name == ".python-version"


/**
 * Whether the given file is `py.typed`.
 */
internal val VirtualFile.isPyTyped: Boolean
    get() = name == "py.typed"


/**
 * Whether the given file's extension is `py`, `pyi` or `pyw`.
 */
internal val VirtualFile.isPythonFile: Boolean
    get() = extension == "py" || extension == "pyi" || extension == "pyw"


/**
 * Whether the given file is `pylock.toml`.
 * 
 * @see isPylockTomlLike
 */
internal val VirtualFile.isPylockToml: Boolean
    get() = name == "pylock.toml"


/**
 * Whether the given file is a lock file as defined by
 * [PEP 751](https://peps.python.org/pep-0751/).
 */
internal val VirtualFile.isPylockTomlLike: Boolean
    get() {
        val fragments = name.split(".")
        
        if (fragments.firstOrNull() != "pylock" || fragments.last() != "toml") {
            return false
        }
        
        return when (fragments.size) {
            2 -> true
            3 -> fragments[1].isNotEmpty()
            else -> false
        }
    }


// TODO: Use `putUserData()`/`getUserData()` instead?
/**
 * @see insyncwithfoo.ryecharm.others.scriptmetadata.EditScriptMetadataFragment.asNewVirtualFile
 */
internal val VirtualFile.isScriptMetadataTemporaryFile: Boolean
    get() = name.startsWith("Script metadata (") && name.endsWith(").toml")


/**
 * Whether any of the following returns `true`:
 * 
 * * [isRuffToml]
 * * [isPyprojectTomlLike]
 */
internal val VirtualFile.mayContainRuffOptions: Boolean
    get() = isRuffToml || isPyprojectTomlLike


/**
 * Whether any of the following returns `true`:
 *
 * * [isPyprojectToml]
 * * [isScriptMetadataTemporaryFile]
 */
internal val VirtualFile.isPyprojectTomlLike: Boolean
    get() = isPyprojectToml || isScriptMetadataTemporaryFile


/**
 * Whether the given file can be checked by Ty.
 */
internal fun VirtualFile.isSupportedByTy(project: Project? = null): Boolean {
    return isPythonFile
}


// TODO: .ipynb / Allow configuring what files
/**
 * Whether the given file is targeted by at least one Ruff lint rule
 * *and* RyeCharm has replicated its support in some manner.
 */
internal fun VirtualFile.canBeLintedByRuff(project: Project? = null): Boolean {
    return isPythonFile || isPyprojectToml
}


/**
 * Whether the given file can be formatted by Ruff's formatter.
 */
internal fun VirtualFile.canBeFormattedByRuff(project: Project? = null): Boolean {
    return isPythonFile
}


// https://github.com/InSyncWithFoo/ryecharm/issues/5
/**
 * Whether the given file's extension is `.rst`.
 */
private val PsiFile.isReST: Boolean
    get() = virtualFile?.extension == "rst"


// https://github.com/InSyncWithFoo/ryecharm/issues/47
/**
 * Whether the given file's extension is `.ipynb`.
 */
private val PsiFile.isJupyter: Boolean
    get() = virtualFile?.extension == "ipynb"


/**
 * Whether the given file is a [PyFile]
 * that contains only Python code.
 */
private val PsiFile.isNormalPyFile: Boolean
    get() = this is PyFile && !this.isReST && !this.isJupyter


/**
 * Shorthand for [VirtualFile.isSupportedByTy].
 */
internal val PsiFile.isSupportedByTy: Boolean
    get() = isNormalPyFile || virtualFile?.isSupportedByTy(project) == true


/**
 * Shorthand for [VirtualFile.canBeLintedByRuff].
 */
internal val PsiFile.canBeLintedByRuff: Boolean
    get() = isNormalPyFile || virtualFile?.canBeLintedByRuff(project) == true


/**
 * Shorthand for [VirtualFile.canBeFormattedByRuff].
 */
internal val PsiFile.canBeFormattedByRuff: Boolean
    get() = isNormalPyFile || virtualFile?.canBeFormattedByRuff(project) == true
