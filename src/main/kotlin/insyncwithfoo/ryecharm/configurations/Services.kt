package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent


/**
 * The base class from which concrete services derive.
 * 
 * Used by various classes and functions as an abstraction.
 * 
 * @see SimplePersistentStateComponent
 */
internal open class ConfigurationService<S : BaseState>(state: S) : SimplePersistentStateComponent<S>(state)
