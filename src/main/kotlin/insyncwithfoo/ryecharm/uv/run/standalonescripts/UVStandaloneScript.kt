package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.copy
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.toPathIfItExists
import insyncwithfoo.ryecharm.uv.run.UVRunConfiguration
import insyncwithfoo.ryecharm.uv.run.ValidationError


internal class UVStandaloneScript(name: String, project: Project, factory: UVStandaloneScriptFactory) :
    UVRunConfiguration<UVStandaloneScriptSettings>(name, project, factory)
{
    
    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        UVStandaloneScriptCommandLineState(settings.copy(), environment)
    
    override fun getConfigurationEditor() =
        UVStandaloneScriptSettingsEditor(settings.copy(), project)
    
    override fun checkConfiguration() {
        if (settings.scriptPath?.toPathIfItExists() == null) {
            throw ValidationError(message("runConfigurations.errors.noScript"))
        }
        
        checkWorkingDirectoryAndEnvironmentVariables()
    }
    
}
