package insyncwithfoo.ryecharm

import org.reflections.Reflections
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter


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


internal inline val <reified T : Any> T.constructorParameters: List<KParameter>
    get() = T::class.constructors.toList<KFunction<Any>>().first().parameters
