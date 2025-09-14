package insyncwithfoo.ryecharm

import com.intellij.openapi.util.registry.Registry
import kotlin.reflect.KProperty0


private interface Prefixed {
    
    val parentPrefix: String?
    val ownPrefix: String
    
    val fullPrefix: String
        get() = listOfNotNull(parentPrefix, ownPrefix).joinToString(".")
    
    private fun key(name: String) =
        "$fullPrefix.$name"
    
    fun key(property: KProperty0<*>) =
        key(property.name)
    
}


internal class Logging(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "logging"
    
    val logCommandsOnOutput: Boolean
        get() = Registry.`is`(key(::logCommandsOnOutput))
    
}


internal class Common(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "common"
    
    val logging = Logging(fullPrefix)
    
}



internal class UV(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "uv"
    
    val alwaysRunUpdater: Boolean
        get() = Registry.`is`(key(::alwaysRunUpdater))
    
    val alwaysRunInstaller: Boolean
        get() = Registry.`is`(key(::alwaysRunInstaller))
    
}


internal class TY(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "ty"
    
    val alwaysRunEnabler: Boolean
        get() = Registry.`is`(key(::alwaysRunEnabler))
    
}


/**
 * Thin wrapper around [Registry] to allow for ergonomic syntax.
 */
internal object RyeCharmRegistry : Prefixed {
    
    override val parentPrefix = null
    override val ownPrefix = RyeCharm.ID
    
    val common = Common(fullPrefix)
    val uv = UV(fullPrefix)
    val ty = TY(fullPrefix)
    
}
