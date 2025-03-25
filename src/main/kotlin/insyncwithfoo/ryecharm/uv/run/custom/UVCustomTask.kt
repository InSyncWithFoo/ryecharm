package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.uv.run.UVRunConfiguration
import kotlin.io.path.isDirectory


internal class UVCustomTask(name: String, project: Project, factory: UVCustomTaskFactory) :
    UVRunConfiguration<UVCustomTaskSettings>(name, project, factory) {
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVCustomTaskCommandLineState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVCustomTaskSettingsEditor(settings.copy(), project)
    
    override fun validateConfiguration(): ValidationResult {
        val workingDirectory = settings.workingDirectory
        
        if (workingDirectory != null) {
            val asPath = workingDirectory.toPathOrNull()
                ?: return ValidationResult.Error(message("runConfigurations.errors.invalidWorkingDirectoryPath"))
            
            if (!asPath.toFile().exists()) {
                return ValidationResult.Error(message("runConfigurations.errors.workingDirectoryDoesNotExist"))
            }
            
            if (!asPath.isDirectory()) {
                return ValidationResult.Error(message("runConfigurations.errors.workingDirectoryIsNotDirectory"))
            }
        }
        
        if (settings.arguments.isNullOrBlank()) {
            return ValidationResult.Warning(message("runConfigurations.errors.noArguments"))
        }
        
        return ValidationResult.OK
    }
    
}
