package insyncwithfoo.ryecharm

import com.intellij.openapi.util.registry.Registry


private interface Prefixed {
    
    val parentPrefix: String?
    val ownPrefix: String
    
    fun key(name: String) =
        listOfNotNull(parentPrefix, ownPrefix, name).joinToString(".")
    
}


internal class UV(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "uv"
    
    val alwaysRunUpdater: Boolean
        get() = Registry.`is`(key("alwaysRunUpdater"))
    
}


/**
 * Thin wrapper around [Registry] to allow for ergonomic syntax.
 */
internal object RyeCharmRegistry : Prefixed {
    
    override val parentPrefix = null
    override val ownPrefix = RyeCharm.ID
    
    val uv = UV(ownPrefix)
    
}
