package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.redhat.devtools.lsp4ij.LanguageServerManager


internal val Project.lspServerManager: LspServerManager
    get() = LspServerManager.getInstance(this)


internal inline fun <reified P : LspServerSupportProvider> LspServerManager.getServers() =
    getServersForProvider(P::class.java)


internal inline fun <reified T : LspServerSupportProvider> Project.restartNativeServers() {
    if (lspIsAvailable && this.isNormal) {
        lspServerManager.stopAndRestartIfNeeded(T::class.java)
    }
}


internal val Project.languageServerManager: LanguageServerManager
    get() = LanguageServerManager.getInstance(this)


private fun Project.stopLSP4IJServers(serverID: String, disable: Boolean = false) {
    val options = LanguageServerManager.StopOptions().apply {
        isWillDisable = disable
    }
    
    languageServerManager.stop(serverID, options)
}


private fun Project.startLSP4IJServers(serverID: String, enable: Boolean = false) {
    val options = LanguageServerManager.StartOptions().apply {
        isWillEnable = enable
    }
    
    languageServerManager.start(serverID, options)
}


internal fun Project.toggleLSP4IJServers(serverID: String, restart: Boolean) {
    if (!lsp4ijIsAvailable) {
        return
    }
    
    stopLSP4IJServers(serverID)
    
    if (restart) {
        startLSP4IJServers(serverID)
    }
}
