package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyFile


internal val VirtualFile.isRyeConfigToml: Boolean
    get() = parent?.name == ".rye" && name == "config.toml"


internal val VirtualFile.isPyprojectToml: Boolean
    get() = name == "pyproject.toml"


internal val VirtualFile.isUVLock: Boolean
    get() = name == "uv.lock"


internal val VirtualFile.isUVToml: Boolean
    get() = name == "uv.toml"


internal val VirtualFile.isRuffToml: Boolean
    get() = name == "ruff.toml" || name == ".ruff.toml"


// https://github.com/InSyncWithFoo/ryecharm/issues/5
private val PsiFile.isReST: Boolean
    get() = virtualFile?.extension == "rst"


// TODO: .ipynb / Allow configuring what files
internal fun VirtualFile.isSupportedByRuff(project: Project? = null): Boolean {
    return extension == "py" || extension == "pyi" || extension == "pyw"
}


internal val PsiFile.isSupportedByRuff: Boolean
    get() = this is PyFile && !this.isReST || virtualFile?.isSupportedByRuff(project) == true
