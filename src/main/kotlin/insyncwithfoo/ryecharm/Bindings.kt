package insyncwithfoo.ryecharm

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.dsl.builder.ButtonsGroup
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.dsl.builder.toNullableProperty
import javax.swing.JComponent
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.full.createInstance


private fun <T> KMutableProperty0<T?>.toNonNullableProperty(getDefaultValue: () -> T): MutableProperty<T> {
    return MutableProperty({ get() ?: getDefaultValue() }, { set(it) })
}


private inline fun <C : JComponent, reified T : Any> Cell<C>.bind(property: KMutableProperty0<T?>): Cell<C> {
    return bind(property.toNullableProperty(defaultValue = T::class.createInstance()))
}


internal fun <C : JComponent> Cell<C>.bindText(property: KMutableProperty0<String?>): Cell<C> {
    return bind(property)
}


internal fun <C : TextFieldWithBrowseButton> Cell<C>.bindText(
    property: KMutableProperty0<String?>,
    getDefaultValue: () -> String
): Cell<C> {
    return bindText(property.toNonNullableProperty(getDefaultValue))
}


internal fun <C : JComponent, T> Cell<C>.bind(property: MutableProperty<T>): Cell<C> {
    val getter: (C) -> T = { _ -> property.get() }
    val setter: (C, T) -> Unit = { _, value -> property.set(value) }
    
    return bind(getter, setter, property)
}


internal fun <C : ComboBox<T>, T : Any> Cell<C>.bindItem(property: KMutableProperty0<T>): Cell<C> {
    return bindItem(property.toNullableProperty())
}


internal inline fun <reified T> ButtonsGroup.bindSelected(property: KMutableProperty0<T>) {
    bind(property.toMutableProperty(), T::class.java)
}


internal fun <C : JBIntSpinner, K> Cell<C>.bindIntValue(map: MutableMap<K, Int>, key: K, defaultValue: Int) {
    bindIntValue({ map[key] ?: defaultValue }, { map[key] = it })
}
