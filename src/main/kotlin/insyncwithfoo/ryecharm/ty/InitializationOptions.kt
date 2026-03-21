package insyncwithfoo.ryecharm.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations


internal data class InitializationOptions(
    var logLevel: String? = null,
    var logFile: String? = null,
)


internal fun Project.createInitializationOptionsObject() = InitializationOptions().apply {
    val configurations = tyConfigurations
    
    logLevel = configurations.logLevel.toString()
    logFile = configurations.logFile
}
