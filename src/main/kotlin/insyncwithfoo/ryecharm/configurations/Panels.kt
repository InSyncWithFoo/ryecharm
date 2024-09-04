package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.project.Project
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.selected
import insyncwithfoo.ryecharm.Commented
import insyncwithfoo.ryecharm.Keyed
import insyncwithfoo.ryecharm.bindIntValue
import insyncwithfoo.ryecharm.configurations.HasTimeouts.Companion.addTimeoutPrefix
import insyncwithfoo.ryecharm.message
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.full.declaredMemberProperties


private val Row.cells: List<Cell<*>>?
    get() {
        val properties = this::class.declaredMemberProperties
        val property = properties.find { it.name == "cells" }
        
        val cells = property?.getter?.call(this) as? List<*>
        
        return cells?.filterIsInstance<Cell<*>>()
    }


private fun <C : JComponent> Cell<C>.forceAlignRight() = this.apply {
    align(AlignX.RIGHT)
    resizableColumn()
}


private fun Panel.visuallySeparatedRow(block: Row.() -> Unit) = row {
    layout(RowLayout.LABEL_ALIGNED)
    topGap(TopGap.SMALL)
    block()
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
    
    fun Row.makeOverrideCheckboxIfApplicable(property: KMutableProperty0<*>) {
        if (projectBased) {
            makeOverrideCheckbox(property.name)
        }
    }
    
    private fun Row.makeTimeoutOverrideCheckbox(settingName: SettingName) {
        makeOverrideCheckbox(settingName.addTimeoutPrefix())
    }
    
    private fun Row.makeOverrideCheckbox(settingName: SettingName) {
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
    fun <E> Panel.makeTimeoutGroup(timeouts: TimeoutMap, entries: List<E>) where E : Commented, E : Keyed {
        collapsibleGroup(message("configurations.timeouts.groupName"), indent = true) {
            row { label(message("configurations.timeouts.note")) }
            
            makeTimeoutRows(timeouts, entries)
        }
    }
    
    private fun <E> Panel.makeTimeoutRows(map: TimeoutMap, entries: List<E>) where E : Commented, E : Keyed {
        val keys = entries.map { it.key }
        val labels = entries.associate { it.key to it.label }
        val comments = entries.associate { it.key to it.comment }
        
        keys.forEach { key ->
            makeTimeoutRow(map, key, labels[key]!!, comments[key]!!)
        }
    }
    
    @Suppress("DialogTitleCapitalization")
    private fun Panel.makeTimeoutRow(map: TimeoutMap, key: SettingName, label: String, comment: String) {
        visuallySeparatedRow {
            makeTimeoutLabel(message("configurations.timeouts.label", label)) {
                comment(message("configurations.timeouts.comment", comment))
            }
            makeTimeoutInput { bindIntValue(map, key, defaultValue = HasTimeouts.NO_LIMIT) }
            label(message("configurations.timeouts.unit"))
            makeTimeoutOverrideCheckbox(key)
        }
    }
    
    private fun Row.makeTimeoutLabel(text: String, block: Cell<JLabel>.() -> Unit) =
        label(text).gap(RightGap.SMALL).apply(block)
    
    private fun Row.makeTimeoutInput(block: Cell<JBIntSpinner>.() -> Unit) =
        spinner(-1..3_600_000, step = 100).forceAlignRight().gap(RightGap.SMALL).apply(block)
    
}
