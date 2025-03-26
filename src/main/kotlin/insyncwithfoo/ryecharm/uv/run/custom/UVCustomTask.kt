package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfiguration
import insyncwithfoo.ryecharm.uv.run.ValidationWarning


internal class UVCustomTask(name: String, project: Project, factory: UVCustomTaskFactory) :
    UVRunConfiguration<UVCustomTaskSettings>(name, project, factory) {
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVCustomTaskCommandLineState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVCustomTaskSettingsEditor(settings.copy(), project)
    
    override fun checkConfiguration() {
        checkWorkingDirectoryAndEnvironmentVariables()
        
        if (settings.arguments.isNullOrBlank()) {
            throw ValidationWarning(message("runConfigurations.errors.noArguments"))
        }
    }
    
}
