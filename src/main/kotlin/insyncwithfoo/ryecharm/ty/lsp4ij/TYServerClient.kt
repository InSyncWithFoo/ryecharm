package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import insyncwithfoo.ryecharm.common.logging.tyLogger
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ty.createInitializationOptionsObject
import insyncwithfoo.ryecharm.ty.createWorkspaceConfigurationObject


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
        project.createInitializationOptionsObject().also {
            val logger = project.tyLogger
            
            logger?.info("Sending initializationOptions:")
            logger?.info("$it")
            logger?.info("")
        }
    
    override fun findSettings(section: String?): Any? {
        val value = when (section) {
            "ty" -> project.createWorkspaceConfigurationObject()
            else -> null
        }
        
        return value.also {
            val logger = project.tyLogger
            
            logger?.info("Sending workspace configuration item for $section:")
            logger?.info("$it")
            logger?.info("")
        }
    }
    
}
