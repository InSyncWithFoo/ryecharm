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
import insyncwithfoo.ryecharm.defaultProject
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.ruff.commands.Category
import insyncwithfoo.ryecharm.ruff.commands.Linter
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runAction
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.unknownError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


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
private class RuffLintersDialog(private val linters: List<Linter>, project: Project) : DialogWrapper(project) {
    
    private var okButtonText: String
        @Deprecated("Getter must not be used")
        get() = throw RuntimeException()
        set(value) = setOKButtonText(value)
    
    private var widthAndHeight: Pair<Int, Int>
        @Deprecated("Getter must not be used")
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
        val ruff = project.ruff ?: return project.unableToRunCommand()
        
        project.runRuffLinterAndShowTable(ruff)
    }
    
    private fun Project.runRuffLinterAndShowTable(ruff: Ruff) = runAction {
        val command = ruff.linter()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                parseOutputAndShowLinters(command, output)
            }
        }
    }
    
    private fun Project.parseOutputAndShowLinters(command: Command, output: ProcessOutput) {
        val linters = parseRuffLinterOutput(output.stdout)
            ?: return unknownError(command, output)
        
        RuffLintersDialog(linters, this).show()
    }
    
    private fun parseRuffLinterOutput(raw: String): List<Linter>? {
        val json = Json { ignoreUnknownKeys = true }
        
        return try {
            json.decodeFromString<List<Linter>>(raw)
        } catch (_: SerializationException) {
            null
        }
    }
    
}
