package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent


internal open class ConfigurationService<S : BaseState>(state: S) : SimplePersistentStateComponent<S>(state)
