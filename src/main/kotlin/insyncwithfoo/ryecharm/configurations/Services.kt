package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.diagnostic.thisLogger
import insyncwithfoo.ryecharm.loaderID


/**
 * The base class from which concrete services derive.
 * 
 * Used by various classes and functions as an abstraction.
 * 
 * @see SimplePersistentStateComponent
 */
internal open class ConfigurationService<S : BaseState>(state: S) :
    SimplePersistentStateComponent<S>(state), Disposable
{
    
    init {
        thisLogger().info("${this::class.qualifiedName} initialized; ${this::class.loaderID}")
        thisLogger().info(Throwable().stackTraceToString())
    }
    
    override fun dispose() {
        thisLogger().info("${this::class.qualifiedName} disposed; ${this::class.loaderID}")
        thisLogger().info(Throwable().stackTraceToString())
    }
    
}
