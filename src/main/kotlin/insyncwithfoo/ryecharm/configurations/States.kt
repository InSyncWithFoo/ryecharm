package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible


/**
 * A setting's name to be stored in `.xml` files.
 * This is also the corresponding Kotlin property's name.
 */
internal typealias SettingName = String

/**
 * A map of names whose corresponding settings' values
 * are to be taken from the project `.xml` file.
 * 
 * The values are always `true`.
 * This is a map rather than a set since
 * sets are only second-class citizens to [BaseState].
 * 
 * @see BaseState.map
 * @see BaseState.stringSet
 */
internal typealias Overrides = MutableMap<SettingName, Boolean>


/**
 * Add [name] to the override map.
 * 
 * Typically used as:
 * 
 * ```kotlin
 * changeToolOverrides { add(::setting.name) }
 * ```
 */
internal fun Overrides.add(name: SettingName) {
    this[name] = true
}


/**
 * Marker for state classes that store [Overrides].
 */
internal interface ProjectOverrideState {
    var names: Overrides
}


/**
 * Marker for state classes to allow them to use [copy] and [copyTo].
 * 
 * @see DisplayableState
 */
internal interface Copyable


/**
 * Create a deep clone of [this].
 * 
 * @see XmlSerializerUtil.createCopy
 */
internal fun <C : Copyable> C.copy(): C {
    return XmlSerializerUtil.createCopy(this)
}


/**
 * Copy property values from [this] to [other] in-place.
 * 
 * @see XmlSerializerUtil.copyBean
 */
internal fun <C : Copyable> C.copyTo(other: C) {
    XmlSerializerUtil.copyBean(this, other)
}


/**
 * The base class from which concrete state classes derive.
 * 
 * This provides copyability via [Copyable] and
 * a [toString] function similar to that of data classes.
 */
internal abstract class DisplayableState : BaseState(), Copyable {
    
    private val displayName: String
        get() {
            val serialNameAnnotation = this::class.annotations.filterIsInstance<SerialName>().firstOrNull()
            return serialNameAnnotation?.value ?: this::class.simpleName!!
        }
    
    override fun toString(): String {
        val properties = this::class.fields
            .map { (name, property) -> "$name=${property.getter.call(this)}" }
            .joinToString(", ")
        
        return "$displayName($properties)"
    }
    
}


private fun <T> KProperty1<T, *>.makeAccessible() =
    this.apply { isAccessible = true }


internal val <S : BaseState> KClass<S>.fields: Map<String, KProperty1<S, *>>
    get() = declaredMemberProperties.map { it.makeAccessible() }.associateBy { it.name }


private inline fun <reified S : Any> applicationService() = service<S>()


/**
 * Merge global and local states into a new state instance.
 * 
 * @receiver The global state. Will be copied.
 */
private inline fun <reified S : DisplayableState> S.mergeWith(other: S, overrides: Overrides): S {
    val all = this.copy()
    
    S::class.fields.forEach { (name, property) ->
        if (property !is KMutableProperty1 || name !in overrides) {
            return@forEach
        }
        
        val overriddenValue = property.get(other)
        property.setter.call(all, overriddenValue)
    }
    
    return all
}


/**
 * Thin wrapper around [mergeWith] that allows for ergonomic syntax.
 */
internal inline fun <
    reified GlobalService : ConfigurationService<S>,
    reified LocalService : ConfigurationService<S>,
    reified OverrideService : ConfigurationService<out ProjectOverrideState>,
    reified S : DisplayableState
> Project.getMergedState(): S {
    val globalState = applicationService<GlobalService>().getState()
    val projectState = service<LocalService>().getState()
    val overrides = service<OverrideService>().getState().names
    
    return globalState.mergeWith(projectState, overrides)
}
