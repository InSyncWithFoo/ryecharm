package insyncwithfoo.ryecharm.uv.run.custom

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.toPathOrNull
import kotlin.io.path.isDirectory


internal class UVCustomTask(name: String, project: Project, factory: UVCustomTaskFactory) :
    RunConfigurationBase<UVCustomTaskSettings>(project, factory, name) {
    
    val settings: UVCustomTaskSettings
        get() = options as UVCustomTaskSettings
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVCustomTaskCommandState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVCustomTaskSettingsEditor(settings.copy(), project)
    
    override fun checkConfiguration() {
        val workingDirectory = settings.workingDirectory
        
        if (workingDirectory != null) {
            val asPath = workingDirectory.toPathOrNull()
                ?: throw RuntimeConfigurationError(message("runConfigurations.errors.invalidWorkingDirectoryPath"))
            
            if (!asPath.toFile().exists()) {
                throw RuntimeConfigurationException(message("runConfigurations.errors.workingDirectoryDoesNotExist"))
            }
            
            if (!asPath.isDirectory()) {
                throw RuntimeConfigurationException(message("runConfigurations.errors.workingDirectoryIsNotDirectory"))
            }
        }
        
        if (settings.arguments.isNullOrBlank()) {
            throw RuntimeConfigurationException(message("runConfigurations.errors.noArguments"))
        }
    }
    
}
