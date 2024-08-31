package insyncwithfoo.ryecharm

import com.intellij.execution.wsl.target.WslTargetEnvironmentConfiguration
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.platform.lsp.api.LspServerSupportProvider


internal val lsp4ijIsAvailable: Boolean
    get() {
        val pluginID = PluginId.getId("com.redhat.devtools.lsp4ij")
        return PluginManagerCore.run { isPluginInstalled(pluginID) && !isDisabled(pluginID) }
    }


@Suppress("UnstableApiUsage")
internal val lspIsAvailable by lazy {
    try {
        LspServerSupportProvider
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}


@Suppress("UNUSED_EXPRESSION")
internal val wslIsSupported by lazy {
    SystemInfo.isWindows && try {
        WslTargetEnvironmentConfiguration::class
        true
    } catch (_: NoClassDefFoundError) {
        false
    }
}
