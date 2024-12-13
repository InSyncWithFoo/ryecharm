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


private val Row.cells: List<Cell<*>>?
    get() {
        val properties = this::class.declaredMemberProperties
        val property = properties.find { it.name == "cells" }
        
        val cells = property?.getter?.call(this) as? List<*>
        
        return cells?.filterIsInstance<Cell<*>>()
    }


private fun Row.rightAligningOverrideCheckbox(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.override.label")).align(AlignX.RIGHT).apply(block)


private fun Row.toggleOtherCellsBasedOn(checkbox: Cell<JBCheckBox>) {
    cells?.forEach {
        it.takeIf { it !== checkbox }?.enabledIf(checkbox.selected)
    }
}


private fun Overrides.toggle(element: SettingName, add: Boolean) {
    when {
        add -> add(element)
        else -> remove(element)
    }
}


/**
 * Generate configuration panels for [PanelBasedConfigurable]s,
 * adding "Override" checkboxes for project ones.
 */
internal abstract class AdaptivePanel<S>(val state: S, private val overrides: Overrides?, val project: Project?) {
    
    private val projectBased: Boolean
        get() = project != null
    
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
    
    @Suppress("DialogTitleCapitalization")
    fun Panel.advancedSettingsGroup(init: Panel.() -> Unit) {
        collapsibleGroup(message("configurations.groups.advanced"), init = init)
    }
    
}
