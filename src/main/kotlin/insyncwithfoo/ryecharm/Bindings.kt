package insyncwithfoo.ryecharm

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TextAccessor
import com.intellij.ui.dsl.builder.ButtonsGroup
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.dsl.builder.toNonNullableProperty
import com.intellij.ui.dsl.builder.toNullableProperty
import javax.swing.JComponent
import kotlin.reflect.KMutableProperty0


internal fun <C> Cell<C>.bindText(property: KMutableProperty0<String?>) where C : JComponent, C : TextAccessor =
    bind(TextAccessor::getText, TextAccessor::setText, property.toNonNullableProperty(""))


internal fun <C : ComboBox<T>, T : Any> Cell<C>.bindItem(property: KMutableProperty0<T>) =
    bindItem(property.toNullableProperty())


internal inline fun <reified T> ButtonsGroup.bindSelected(property: KMutableProperty0<T>) =
    bind(property.toMutableProperty(), T::class.java)
