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


// TODO: .ipynb / Allow configuring what files
/**
 * Whether the given file is supported by Ruff
 * *and* RyeCharm has replicated its support in some manner.
 */
internal fun VirtualFile.isSupportedByRuff(project: Project? = null): Boolean {
    return extension == "py" || extension == "pyi" || extension == "pyw"
}


/**
 * Shorthand for [VirtualFile.isSupportedByRuff].
 */
internal val PsiFile.isSupportedByRuff: Boolean
    get() = this is PyFile && !this.isReST && !this.isJupyter
        || virtualFile?.isSupportedByRuff(project) == true
