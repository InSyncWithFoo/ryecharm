package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.configurations.RuntimeConfigurationWarning
import com.intellij.openapi.project.Project


/**
 * The base class from which concrete [RunConfiguration] classes derive.
 * 
 * A run configuration instance is bound to its [settings].
 * [getState] and [getConfigurationEditor] shall pass these settings
 * to the corresponding [RunProfileState] and [PanelBasedSettingsEditor],
 * but in doing so they must make copies.
 */
internal abstract class UVRunConfiguration<S : CopyableRunConfigurationSettings>(
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
    
    final override fun checkConfiguration() {
        when (val result = validateConfiguration()) {
            ValidationResult.OK -> {}
            is ValidationResult.WeakWarning -> throw RuntimeConfigurationWarning(result.message)
            is ValidationResult.Warning -> throw RuntimeConfigurationException(result.message)
            is ValidationResult.Error -> throw RuntimeConfigurationError(result.message)
        }
    }
    
    /**
     * @see ValidationResult
     */
    protected abstract fun validateConfiguration(): ValidationResult
    
    /**
     * @see checkConfiguration
     * @see RunConfigurationBase.checkConfiguration
     */
    protected sealed class ValidationResult {
        data object OK : ValidationResult()
        class WeakWarning(val message: String) : ValidationResult()
        class Warning(val message: String) : ValidationResult()
        class Error(val message: String) : ValidationResult()
    }
    
}
