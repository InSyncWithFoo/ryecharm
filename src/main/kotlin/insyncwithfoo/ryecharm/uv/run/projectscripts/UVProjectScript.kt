package insyncwithfoo.ryecharm.uv.run.projectscripts

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.uv.run.UVRunConfiguration
import insyncwithfoo.ryecharm.uv.run.ValidationError


internal class UVProjectScript(name: String, project: Project, factory: UVProjectScriptFactory) :
    UVRunConfiguration<UVProjectScriptSettings>(name, project, factory) {
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVProjectScriptCommandLineState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVProjectScriptSettingsEditor(settings.copy(), project)
    
    override fun checkConfiguration() {
        if (settings.scriptName.isNullOrBlank()) {
            throw ValidationError(message("runConfigurations.errors.noScript"))
        }
        
        checkWorkingDirectoryAndEnvironmentVariables()
    }
    
}
