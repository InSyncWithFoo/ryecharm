package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.openapi.vfs.VirtualFile


// TODO: Use `putUserData()`/`getUserData()` instead?
/**
 * @see EditScriptMetadataFragment.asNewVirtualFile
 */
internal val VirtualFile.isScriptMetadataTemporaryFile: Boolean
    get() = name.startsWith("Script metadata (") && name.endsWith(").toml")
