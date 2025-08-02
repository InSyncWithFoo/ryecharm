package insyncwithfoo.ryecharm

import com.intellij.openapi.util.registry.Registry
import kotlin.reflect.KProperty0


private interface Prefixed {
    
    val parentPrefix: String?
    val ownPrefix: String
    
    private fun key(name: String) =
        listOfNotNull(parentPrefix, ownPrefix, name).joinToString(".")
    
    fun key(property: KProperty0<*>) =
        key(property.name)
    
}


internal class UV(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "uv"
    
    val alwaysRunUpdater: Boolean
        get() = Registry.`is`(key(::alwaysRunUpdater))
    
    val alwaysRunInstaller: Boolean
        get() = Registry.`is`(key(::alwaysRunInstaller))
    
}


/**
 * Thin wrapper around [Registry] to allow for ergonomic syntax.
 */
internal object RyeCharmRegistry : Prefixed {
    
    override val parentPrefix = null
    override val ownPrefix = RyeCharm.ID
    
    val uv = UV(ownPrefix)
    
}
