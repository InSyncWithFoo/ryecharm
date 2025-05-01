package insyncwithfoo.ryecharm.uv.run.globaltools

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfiguration
import insyncwithfoo.ryecharm.uv.run.ValidationError


internal class UVXTool(name: String, project: Project, factory: UVXToolFactory) :
    UVRunConfiguration<UVXToolSettings>(name, project, factory)
{
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVXToolCommandLineState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVXToolSettingsEditor(settings.copy(), project)
    
    override fun checkConfiguration() {
        if (settings.toolName.isNullOrBlank()) {
            throw ValidationError(message("runConfigurations.errors.noTool"))
        }
        
        checkWorkingDirectoryAndEnvironmentVariables()
    }
    
}
