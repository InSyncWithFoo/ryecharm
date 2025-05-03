package insyncwithfoo.ryecharm.redknot.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import insyncwithfoo.ryecharm.common.logging.redKnotLogger
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.configurations.ty.redKnotConfigurations
import insyncwithfoo.ryecharm.path


internal class RedKnotServerClient(project: Project) : LanguageClientImpl(project) {
    
    init {
        val logger = project.redKnotLogger
        
        logger?.info("Starting Red Knot's language server (LSP4IJ).")
        logger?.info("")
        logger?.info("Executable: ${project.redKnotExecutable}")
        logger?.info("Working directory: ${project.path}")
        logger?.info("Configurations: ${project.redKnotConfigurations}")
        logger?.info("")
    }
    
    override fun createSettings() =
        Object()
    
}
