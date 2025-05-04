package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import insyncwithfoo.ryecharm.common.logging.tyLogger
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.path


internal class TYServerClient(project: Project) : LanguageClientImpl(project) {
    
    init {
        val logger = project.tyLogger
        
        logger?.info("Starting ty's language server (LSP4IJ).")
        logger?.info("")
        logger?.info("Executable: ${project.tyExecutable}")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: ${project.tyConfigurations}")
        logger?.info("")
    }
    
    override fun createSettings() =
        Object()
    
}
