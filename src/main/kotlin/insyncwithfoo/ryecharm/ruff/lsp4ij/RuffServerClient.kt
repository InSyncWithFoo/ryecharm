package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import insyncwithfoo.ryecharm.ruff.createInitializationOptionsObject


internal class RuffServerClient(project: Project) : LanguageClientImpl(project) {
    
    override fun createSettings() =
        project.createInitializationOptionsObject().also { LOGGER.info(it.toString()) }
    
    companion object {
        private val LOGGER = Logger.getInstance(RuffServerClient::class.java)
    }
    
}
