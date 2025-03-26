package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.configurations.RuntimeConfigurationWarning
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.toPathOrNull
import kotlin.io.path.isDirectory


internal typealias ValidationWeakWarning = RuntimeConfigurationWarning
internal typealias ValidationWarning = RuntimeConfigurationException
internal typealias ValidationError = RuntimeConfigurationError


/**
 * The base class from which concrete [RunConfiguration] classes derive.
 * 
 * A run configuration instance is bound to its [settings].
 * [getState] and [getConfigurationEditor] shall pass these settings
 * to the corresponding [RunProfileState] and [PanelBasedSettingsEditor],
 * but in doing so they must make copies.
 */
internal abstract class UVRunConfiguration<S : UVRunConfigurationSettings>(
    name: String,
    project: Project,
    factory: UVRunConfigurationFactory<S, out UVRunConfiguration<S>>
) : RunConfigurationBase<S>(project, factory, name) {
    
    /**
     * The settings bound to this run configuration,
     * downcasted to [S].
     * 
     * This operation is safe since [myOptions]
     * is always an instance of the class returned by
     * [UVRunConfigurationFactory.getOptionsClass].
     */
    @Suppress("UNCHECKED_CAST")
    val settings: S
        get() = options as S
    
    abstract override fun checkConfiguration()
    
    protected fun checkWorkingDirectoryAndEnvironmentVariables() {
        val workingDirectory = settings.workingDirectory ?: return
        
        val asPath = workingDirectory.toPathOrNull()
            ?: throw ValidationError(message("runConfigurations.errors.invalidWorkingDirectoryPath"))
        
        if (!asPath.toFile().exists()) {
            throw ValidationError(message("runConfigurations.errors.workingDirectoryDoesNotExist"))
        }
        
        if (!asPath.isDirectory()) {
            throw ValidationError(message("runConfigurations.errors.workingDirectoryIsNotDirectory"))
        }
    }
    
}
