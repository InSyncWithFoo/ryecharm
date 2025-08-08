package insyncwithfoo.ryecharm

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.psi.PsiElement
import com.jetbrains.python.sdk.PythonSdkUtil
import com.jetbrains.python.sdk.basePath
import java.nio.file.Path


private val Module?.pythonSDK: Sdk?
    get() = PythonSdkUtil.findPythonSdk(this)


/**
 * Whether the module SDK is of uv flavor.
 *
 * @see pythonSDK
 * @see isUV
 */
internal val Module.sdkIsUV: Boolean
    get() = pythonSDK?.isUV == true


internal val Module.rootManager: ModuleRootManager
    get() = ModuleRootManager.getInstance(this)


internal val Module.path: Path?
    get() = basePath?.toPathOrNull()


internal val Module.interpreterPath: Path?
    get() = pythonSDK?.homePath?.let { Path.of(it) } ?: project.interpreterPath


internal val PsiElement.module: Module?
    get() = ModuleUtilCore.findModuleForPsiElement(this) ?: project.modules.singleOrNull()
