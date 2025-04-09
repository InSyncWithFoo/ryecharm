package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import kotlin.reflect.KClass


private val Project.runManager: RunManager
    get() = RunManager.getInstance(this)


internal abstract class UVRunConfigurationProducer<C : UVRunConfiguration<*>> : LazyRunConfigurationProducer<C>() {
    
    abstract val runConfigurationClass: KClass<C>
    
    // https://stackoverflow.com/a/41222239
    private fun RunConfiguration.castToOwnType(): C? =
        try {
            runConfigurationClass.javaObjectType.cast(this)
        } catch (_: ClassCastException) {
            null
        }
    
    /**
     * Find an existing run configuration that corresponds to [context].
     * 
     * The super implementation calls [isConfigurationFromContext]
     * indiscriminately, causing [ClassCastException] to be thrown.
     * This method helps avoiding that by skipping run configurations
     * that cannot be casted to [runConfigurationClass].
     * 
     * @see RunConfigurationProducer.findExistingConfiguration
     */
    override fun findExistingConfiguration(context: ConfigurationContext): RunnerAndConfigurationSettings? {
        val runManager = context.project.runManager
        
        return getConfigurationSettingsList(runManager).firstOrNull {
            ProgressManager.checkCanceled()
            
            when (val runConfiguration = it.configuration.castToOwnType()) {
                null -> false
                else -> isConfigurationFromContext(runConfiguration, context)
            }
        }
    }
    
}
