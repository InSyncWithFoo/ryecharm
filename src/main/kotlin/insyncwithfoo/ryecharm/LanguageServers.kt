package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.redhat.devtools.lsp4ij.LanguageServerManager


internal val Project.lspClientManager: LspClientManager
    get() = LspClientManager.getInstance(this)


internal inline fun <reified T : LspIntegrationProvider> Project.restartNativeServers() {
    if (lspIsAvailable) {
        lspClientManager.stopAndRestartClientsIfNeeded(T::class.java)
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
