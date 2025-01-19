package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.scale.JBUIScale.scale
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.parseAsJSONLeniently
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unknownError
import kotlinx.serialization.Serializable
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


@Serializable
private data class Category(
    val prefix: String,
    val name: String
)


@Serializable
private data class Linter(
    val prefix: String,
    val name: String,
    val categories: List<Category>? = null
)


private val Linter.categoriesOrSynthesized: List<Category>
    get() = categories ?: listOf(Category(prefix = "", name = ""))


private data class LinterTableRow(
    val name: String,
    val prefix: String,
    val linterName: String,
    val linterPrefix: String
)


private fun LinterTableRow(linter: Linter, category: Category) = LinterTableRow(
    name = category.name,
    prefix = category.prefix,
    linterName = linter.name,
    linterPrefix = linter.prefix
)


private fun column(header: String, getText: (LinterTableRow) -> String) =
    object : ColumnInfo<LinterTableRow, String>(header) {
        override fun valueOf(item: LinterTableRow) = getText(item)
    }


/**
 * @see com.intellij.ide.actions.AboutDialog.showOssInfo
 */
private class LintersDialog(private val linters: List<Linter>, project: Project) : DialogWrapper(project) {
    
    private var okButtonText: String
        @Deprecated("The getter must not be used.", level = DeprecationLevel.ERROR)
        get() = throw RuntimeException()
        set(value) = setOKButtonText(value)
    
    private var widthAndHeight: Pair<Int, Int>
        @Deprecated("The getter must not be used.", level = DeprecationLevel.ERROR)
        get() = throw RuntimeException()
        set(value) {
            val (width, height) = value
            setSize(width, height)
        }
    
    private val columns = arrayOf<ColumnInfo<LinterTableRow, String>>(
        column(message("dialogs.ruffLinters.linterName")) { it.linterName },
        column(message("dialogs.ruffLinters.linterPrefix")) { it.linterPrefix },
        column(message("dialogs.ruffLinters.categoryName")) { it.name },
        column(message("dialogs.ruffLinters.categoryPrefix")) { it.prefix }
    )
    
    init {
        init()
        
        @Suppress("DialogTitleCapitalization")
        title = message("dialogs.ruffLinters.title")
        
        isAutoAdjustable = true
        okButtonText = message("dialogs.actions.close")
        widthAndHeight = scale(750) to scale(650)
    }
    
    
    override fun createActions() = arrayOf(okAction)
    
    override fun createCenterPanel(): JComponent {
        val selectedColumn = 0
        val rows = linters.flatMap { linter ->
            linter.categoriesOrSynthesized.map { category ->
                LinterTableRow(linter, category)
            }
        }
        
        val table = TableView(ListTableModel(columns, rows, selectedColumn))
        val controlsPanel = JPanel(VerticalFlowLayout())
        
        return JPanel(BorderLayout()).apply {
            add(controlsPanel, BorderLayout.NORTH)
            add(ScrollPaneFactory.createScrollPane(table), BorderLayout.CENTER)
        }
    }
    
}


internal class ShowLinters : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val ruff = project.ruff
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to retrieve upstream linters information.
                """.trimMargin()
            )
            return
        }
        
        project.runRuffLinterAndShowTable(ruff)
    }
    
    private fun Project.runRuffLinterAndShowTable(ruff: Ruff) = launch<ActionCoroutine> {
        val command = ruff.linter()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                parseOutputAndShowLinters(command, output)
            }
        }
    }
    
    private fun Project.parseOutputAndShowLinters(command: Command, output: ProcessOutput) {
        val linters = output.stdout.parseAsJSONLeniently<List<Linter>>()
            ?: return unknownError(command, output)
        
        LintersDialog(linters, this).show()
    }
    
}
