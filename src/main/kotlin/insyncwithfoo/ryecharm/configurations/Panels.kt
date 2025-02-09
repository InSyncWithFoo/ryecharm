package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.selected
import insyncwithfoo.ryecharm.message
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.full.declaredMemberProperties


/**
 * Return the cells belong to this [Row].
 * 
 * Since `RowImpl.cells` is not visible,
 * this provides a workaround using reflection.
 */
private val Row.cells: List<Cell<*>>?
    get() {
        val properties = this::class.declaredMemberProperties
        val property = properties.find { it.name == "cells" }
        
        val cells = property?.getter?.call(this) as? List<*>
        
        return cells?.filterIsInstance<Cell<*>>()
    }


private fun Overrides.toggle(element: SettingName, add: Boolean) {
    when {
        add -> add(element)
        else -> remove(element)
    }
}


/**
 * The base class from which concrete panel classes derive.
 * 
 * Each such class are defined in its own file along with
 * a `makeComponent` extension function that creates different panel components
 * based on whether it is given [ProjectBasedConfigurable] or otherwise.
 * 
 * Other extension functions making up that panel's subcomponents
 * are also defined in the same file.
 * 
 * @see overrideCheckbox
 */
internal abstract class AdaptivePanel<S>(val state: S, private val overrides: Overrides?, val project: Project?) {
    
    private val projectBased: Boolean
        get() = project != null
    
    /**
     * Declare an "Override" checkbox that binds itself to the given [property]'s name,
     * but only if the current [AdaptivePanel] is that of a [ProjectBasedConfigurable].
     */
    fun Row.overrideCheckbox(property: KMutableProperty0<*>) {
        if (projectBased) {
            overrideCheckbox(property.name)
        }
    }
    
    private fun Row.overrideCheckbox(settingName: SettingName) {
        val overrides = overrides ?: return
        
        val checkbox = rightAligningOverrideCheckbox {
            bindSelected(
                { settingName in overrides },
                { addOrRemove -> overrides.toggle(settingName, addOrRemove) }
            )
        }
        
        toggleOtherCellsBasedOn(checkbox)
    }
    
    private fun Row.rightAligningOverrideCheckbox(block: Cell<JBCheckBox>.() -> Unit) =
        checkBox(message("configurations.override.label")).align(AlignX.RIGHT).apply(block)
    
    /**
     * Attach a callback to each cell retrieved using [Row.cells]
     * that will disable/enable the cell according to the [checkbox]'s state.
     */
    private fun Row.toggleOtherCellsBasedOn(checkbox: Cell<JBCheckBox>) {
        cells?.forEach {
            it.takeIf { it !== checkbox }?.enabledIf(checkbox.selected)
        }
    }
    
    /**
     * Declare an "Advanced settings" collapsible group.
     * 
     * By default, the group is collapsed.
     */
    @Suppress("DialogTitleCapitalization")
    fun Panel.advancedSettingsGroup(init: Panel.() -> Unit) {
        collapsibleGroup(message("configurations.groups.advanced"), init = init)
    }
    
}
