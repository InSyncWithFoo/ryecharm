package insyncwithfoo.ryecharm

import com.intellij.openapi.util.registry.Registry


private interface Prefixed {
    
    val parentPrefix: String?
    val ownPrefix: String
    
    fun key(name: String) =
        listOfNotNull(parentPrefix, ownPrefix, name).joinToString(".")
    
}


internal class Logging(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "logging"
    
    val commands: Boolean
        get() = Registry.`is`(key("commands"))
    
}


internal class RedKnot(override val parentPrefix: String) : Prefixed {
    
    override val ownPrefix = "redknot"
    
    val panels: Boolean
        get() = Registry.`is`(key("panels"))
    
}


/**
 * Thin wrapper around [Registry] to allow for ergonomic syntax.
 */
internal object RyeCharmRegistry : Prefixed {
    
    override val parentPrefix = null
    override val ownPrefix = RyeCharm.ID
    
    val logging = Logging(ownPrefix)
    val redknot = RedKnot(ownPrefix)
    
}
