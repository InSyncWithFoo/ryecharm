package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import insyncwithfoo.ryecharm.common.logging.ruffLogger
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.createInitializationOptionsObject


internal class RuffServerClient(project: Project) : LanguageClientImpl(project) {
    
    init {
        val logger = project.ruffLogger
        
        logger?.info("Starting Ruff's language server (LSP4IJ).")
        logger?.info("")
        logger?.info("Executable: ${project.ruffExecutable}")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: ${project.ruffConfigurations}")
        logger?.info("")
    }
    
    override fun createSettings() =
        project.createInitializationOptionsObject().also {
            val logger = project.ruffLogger
            
            logger?.info("Sending initializationOptions:")
            logger?.info("$it")
            logger?.info("")
        }
    
}
