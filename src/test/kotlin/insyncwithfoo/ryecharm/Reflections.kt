package insyncwithfoo.ryecharm

import org.reflections.Reflections
import java.lang.reflect.Modifier


private inline fun <T> List<T>.filterIf(condition: Boolean, predicate: (T) -> Boolean) =
    when (condition) {
        true -> this.filter(predicate)
        else -> this
    }


private val Class<*>.isConcrete: Boolean
    get() = !isInterface && !Modifier.isAbstract(modifiers)


internal val `package`: Reflections
    get() = Reflections(RyeCharm.ID)


internal inline fun <reified C> Reflections.getSubtypesOf(concrete: Boolean = true) =
    getSubTypesOf(C::class.java)
        .filter { it!!.packageName.startsWith(RyeCharm.ID) }
        .filterIf(concrete) { it.isConcrete }


internal inline fun <reified Super> Class<*>.isSubtypeOf() =
    Super::class.java.isAssignableFrom(this)
