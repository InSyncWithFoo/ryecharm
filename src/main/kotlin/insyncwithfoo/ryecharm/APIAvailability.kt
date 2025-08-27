package insyncwithfoo.ryecharm

import com.intellij.execution.wsl.target.WslTargetEnvironmentConfiguration
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.platform.lsp.api.LspServerSupportProvider


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
 * it is part of IntelliJ IDEA Ultimate, WebStorm, PhpStorm, PyCharm Professional,
 * DataSpell, RubyMine, CLion, Aqua, DataGrip, GoLand, Rider, and RustRover.
 */
@Suppress("UnusedExpression")
internal val lspIsAvailable by lazy {
    try {
        LspServerSupportProvider
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}


/**
 * Whether the IDE has WSL-specific support.
 */
@Suppress("UNUSED_EXPRESSION")
internal val wslIsSupported by lazy {
    SystemInfo.isWindows && try {
        WslTargetEnvironmentConfiguration::class
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}
