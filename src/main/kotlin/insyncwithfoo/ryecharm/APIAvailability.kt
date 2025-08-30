package insyncwithfoo.ryecharm

import com.intellij.execution.wsl.target.WslTargetEnvironmentConfiguration
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.platform.lsp.api.LspServerSupportProvider


// https://stackoverflow.com/q/79750919
/**
 * Loads the class [C].
 * 
 * This (inline) function is necessary because
 * a plain reference would otherwise be optimized away.
 */
private inline fun <reified C> load() {
    C::class.qualifiedName
}


/**
 * Whether LSP4IJ is both installed and enabled.
 */
internal val lsp4ijIsAvailable: Boolean
    get() {
        val pluginID = PluginId.getId("com.redhat.devtools.lsp4ij")
        return PluginManagerCore.run { isPluginInstalled(pluginID) && !isDisabled(pluginID) }
    }


/**
 * Whether the native client is available.
 * 
 * According to [the docs](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html#supported-ides),
 * it is part of IntelliJ IDEA Ultimate, WebStorm, PhpStorm, Unified PyCharm,
 * DataSpell, RubyMine, CLion, Aqua, DataGrip, GoLand, Rider, and RustRover.
 */
internal val lspIsAvailable by lazy {
    try {
        load<LspServerSupportProvider>()
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}


/**
 * Whether the IDE has WSL-specific support.
 */
internal val wslIsSupported by lazy {
    SystemInfo.isWindows && try {
        load<WslTargetEnvironmentConfiguration>()
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}
