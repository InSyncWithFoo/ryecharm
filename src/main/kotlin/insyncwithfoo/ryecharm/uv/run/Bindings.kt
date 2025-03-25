package insyncwithfoo.ryecharm.uv.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.toMutableProperty
import kotlin.reflect.KMutableProperty0


private typealias EnvironmentVariablesEditor = EnvironmentVariablesTextFieldWithBrowseButton
private typealias EnvironmentVariables = MutableMap<String, String>


internal fun <C : RawCommandLineEditor> Cell<C>.bindText(property: KMutableProperty0<String?>): Cell<C> {
    val getter: (C) -> String? = { component -> component.text }
    val setter: (C, String?) -> Unit = { component, value -> component.text = value }
    
    return bind(getter, setter, property.toMutableProperty())
}


internal fun <C : EnvironmentVariablesEditor> Cell<C>.bind(property: KMutableProperty0<EnvironmentVariables>): Cell<C> {
    val getter: (C) -> EnvironmentVariables = { component -> component.envs }
    val setter: (C, EnvironmentVariables) -> Unit = { component, value -> component.envs = value }
    
    return bind(getter, setter, property.toMutableProperty())
}
