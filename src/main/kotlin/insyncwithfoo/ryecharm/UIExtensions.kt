package insyncwithfoo.ryecharm

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.emptyText
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindText
import javax.swing.JComponent


internal fun Row.singleFileTextField(): Cell<TextFieldWithBrowseButton> {
    val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
    val (project, fileChosen) = Pair(null, null)
    
    return textFieldWithBrowseButton(fileChooserDescriptor, project, fileChosen)
}


internal fun Row.singleFolderTextField(): Cell<TextFieldWithBrowseButton> {
    val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
    val (project, fileChosen) = Pair(null, null)
    
    return textFieldWithBrowseButton(fileChooserDescriptor, project, fileChosen)
}


internal fun <C : JComponent> Cell<C>.topLabel(label: String) =
    label(label, position = LabelPosition.TOP)


internal fun <C : JComponent> Cell<C>.makeFlexible() = apply {
    align(AlignX.FILL)
    resizableColumn()
}


internal var Cell<TextFieldWithBrowseButton>.emptyText: String
    @Deprecated("The getter must not be used.", level = DeprecationLevel.ERROR)
    get() = throw RuntimeException()
    set(value) {
        component.emptyText.text = value
    }


internal fun Row.reactiveLabel(property: ObservableMutableProperty<String>, maxLineLength: Int = 60) =
    comment("", maxLineLength).bindText(property)


internal interface Keyed {
    val key: String
}


internal interface Labeled {
    val label: String
}


internal interface Commented : Labeled {
    val comment: String
}


internal inline fun <reified L : Labeled> createListCellRenderer() =
    SimpleListCellRenderer.create<L> { label, item, _ ->
        label.text = item.label
    }


internal inline fun <reified E> Row.comboBox() where E : Enum<E>, E : Labeled =
    comboBox(enumValues<E>().toList(), createListCellRenderer<E>())


internal fun Row.radioButtonFor(item: Labeled) =
    radioButton(item.label, item)


internal fun Row.radioButtonFor(item: Labeled, getContextDependentLabel: (String) -> String?) =
    radioButton(getContextDependentLabel(item.label) ?: item.label, item)
