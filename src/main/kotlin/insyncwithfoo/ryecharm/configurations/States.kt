package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.HasTimeouts.Companion.removeTimeoutPrefix
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible


internal typealias SettingName = String
internal typealias Overrides = MutableMap<SettingName, Boolean>
internal typealias TimeoutMap = MutableMap<SettingName, MillisecondsOrNoLimit>


internal fun Overrides.add(name: SettingName) {
    this[name] = true
}


internal interface ProjectOverrideState {
    var names: Overrides
}


internal interface Copyable


internal fun <C : Copyable> C.copy(): C {
    return XmlSerializerUtil.createCopy(this)
}


internal abstract class DisplayableState : BaseState(), Copyable {
    
    private val displayName: String
        get() {
            val serialNameAnnotation = this::class.annotations.find { it is SerialName } as? SerialName
            return serialNameAnnotation?.value ?: this::class.simpleName!!
        }
    
    override fun toString(): String {
        val properties = this::class.fields
            .map { (name, property) -> "$name=${property.getter.call(this)}" }
            .joinToString(", ")
        
        return "$displayName($properties)"
    }
    
}


internal interface HasTimeouts {
    
    var timeouts: TimeoutMap
    
    companion object {
        
        private const val SETTING_NAME_PREFIX = "timeout."
        
        const val NO_LIMIT = -1
        
        private val SettingName.isTimeoutSetting: Boolean
            get() = this.startsWith(SETTING_NAME_PREFIX)
        
        internal fun SettingName.addTimeoutPrefix(): SettingName =
            SETTING_NAME_PREFIX + this
        
        internal fun SettingName.removeTimeoutPrefix(): SettingName? =
            this.removePrefix(SETTING_NAME_PREFIX).takeIf { this.isTimeoutSetting }
        
    }
    
}


private fun <T> KProperty1<T, *>.makeAccessible() =
    this.apply { isAccessible = true }


internal val <S : BaseState> KClass<S>.fields: Map<String, KProperty1<S, *>>
    get() = declaredMemberProperties.map { it.makeAccessible() }.associateBy { it.name }


private inline fun <reified S : Any> applicationService() = service<S>()


private inline fun <reified S : DisplayableState> S.mergeWithExcludingTimeouts(other: S, overrides: Overrides): S {
    val all = this.copy()
    
    S::class.fields.forEach { (name, property) ->
        if (property !is KMutableProperty1 || name !in overrides) {
            return@forEach
        }
        
        if (this is HasTimeouts && name == ::timeouts.name) {
            return@forEach
        }
        
        val overriddenValue = property.get(other)
        property.setter.call(all, overriddenValue)
    }
    
    return all
}


/**
 * Merge global and local states into a new state instance.
 * 
 * @receiver The global state. Will be copied.
 */
private inline fun <reified S : DisplayableState> S.mergeWith(other: S, overrides: Overrides): S {
    val all = this.mergeWithExcludingTimeouts(other, overrides)
    
    if (this is HasTimeouts && other is HasTimeouts && all is HasTimeouts) {
        val timeoutOverrides = overrides.keys.mapNotNull { it.removeTimeoutPrefix() }
        val overridden = other.timeouts.filterKeys { it in timeoutOverrides }
        
        all.timeouts = (this.timeouts + overridden).toMutableMap()
    }
    
    return all
}


/**
 * Thin wrapper around [mergeWith].
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
