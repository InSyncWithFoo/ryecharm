package insyncwithfoo.ryecharm.uv.generator

import com.intellij.ide.IdeBundle
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.bind
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.PyBundle
import com.jetbrains.python.PySdkBundle
import com.jetbrains.python.sdk.add.ExistingPySdkComboBoxItem
import com.jetbrains.python.sdk.add.PySdkComboBoxItem
import com.jetbrains.python.sdk.add.PySdkPathChoosingComboBox
import com.jetbrains.python.sdk.add.addInterpretersAsync
import com.jetbrains.python.sdk.detectSystemWideSdks
import insyncwithfoo.ryecharm.bindSelected
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.configurations.globalUVExecutable
import insyncwithfoo.ryecharm.isNonEmptyDirectory
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.reactiveLabel
import insyncwithfoo.ryecharm.singleFileTextField
import insyncwithfoo.ryecharm.singleFolderTextField
import insyncwithfoo.ryecharm.toPathOrNull
import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable
import kotlin.io.path.isRegularFile


private val pep508DistributionName = """(?ix)
    ^
    [A-Z0-9]
    (?:[A-Z0-9._-]*[A-Z0-9])?
    ${"$"}
""".toRegex()


private fun PySdkPathChoosingComboBox.addInterpreters(obtainer: () -> List<Sdk>) {
    addInterpretersAsync(this, obtainer)
}


internal fun PySdkPathChoosingComboBox.addSystemWideInterpreters() {
    addInterpreters { detectSystemWideSdks(module = null, existingSdks = emptyList()) }
}


private fun <T> GraphProperty<T>.dependsOn(vararg parents: ObservableProperty<*>, update: () -> T) {
    parents.forEach { parent ->
        dependsOn(parent, update)
    }
}


// TODO: Refactor this
internal class UVProjectSettingsStepPanel(val settings: UVNewProjectSettings) {
    
    private val propertyGraph = PropertyGraph()
    
    val projectName = propertyGraph.property("")
    val projectParentDirectory = propertyGraph.property("")
    
    private val projectPath: Path?
        get() = try {
            Path.of(projectParentDirectory.get(), projectName.get())
        } catch (_: InvalidPathException) {
            null
        }
    
    val projectPathHint = propertyGraph.property("").apply {
        dependsOn(projectName, projectParentDirectory) {
            val projectPath = projectPath
            
            when {
                projectPath == null -> message("newProjectPanel.hint.invalidPath")
                projectPath.isRegularFile() -> message("newProjectPanel.hint.existingFile")
                projectPath.isNonEmptyDirectory() -> message("newProjectPanel.hint.nonEmptyDirectory")
                else -> PyBundle.message("new.project.location.hint", projectPath)
            }
        }
    }
    
    val baseSDKItem = propertyGraph.property<PySdkComboBoxItem?>(null)
    
    val baseSDK: Sdk?
        get() = (baseSDKItem.get() as? ExistingPySdkComboBoxItem)?.sdk
    
    val uvExecutable = propertyGraph.property("")
    
    val uvExecutablePathHint = propertyGraph.property("").apply {
        dependsOn(uvExecutable) {
            val path = uvExecutable.get().toPathOrNull()
            
            when {
                path == null -> message("newProjectPanel.hint.invalidPath")
                !path.isAbsolute -> message("newProjectPanel.hint.nonAbsolutePath")
                !path.exists() -> message("newProjectPanel.hint.notFound")
                path.isDirectory() -> message("newProjectPanel.hint.unexpectedDirectory")
                !path.isExecutable() -> message("newProjectPanel.hint.nonExecutable")
                else -> message("newProjectPanel.hint.fileFound")
            }
        }
    }
    
    val distributionName = propertyGraph.property("")
    
    val distributionNameHint = propertyGraph.property("").apply {
        dependsOn(distributionName, projectName) {
            val name = distributionName.get().ifEmpty { projectName.get() }
            
            when {
                pep508DistributionName.matches(name) -> message("newProjectPanel.hint.validDistributionName")
                else -> message("newProjectPanel.hint.invalidDistributionName")
            }
        }
    }
    
    lateinit var projectLocationInput: TextFieldWithBrowseButton
    
    private val projectPathIsValid: Boolean
        get() = projectPathHint.get() == PyBundle.message("new.project.location.hint", projectPath)
    
    private val baseInterpreterIsValid: Boolean
        get() = baseSDK?.path != null
    
    private val uvExecutablePathIsValid: Boolean
        get() = uvExecutablePathHint.get() == message("newProjectPanel.hint.fileFound")
    
    private val distributionNameIsValid: Boolean
        get() = distributionNameHint.get() == message("newProjectPanel.hint.validDistributionName")
    
    /**
     * @see UVProjectSettingsStep.getProjectLocation
     */
    val projectLocation: String
        get() = FileUtil.expandUserHome(projectParentDirectory.joinCanonicalPath(projectName).get())
            
    val venvCreator: VenvCreator
        get() = VenvCreator(
            uvExecutable = uvExecutable.get().toPathOrNull()!!,
            projectPath = projectPath!!,
            baseSdk = baseSDK!!
        )
    
    fun setNewProjectName(nextProjectName: File) {
        projectName.set(nextProjectName.nameWithoutExtension)
        projectParentDirectory.set(nextProjectName.parent)
    }
    
    fun registerValidator(validate: () -> Unit) {
        val properties = listOf(
            projectName,
            projectParentDirectory,
            baseSDKItem,
            uvExecutable,
            distributionName
        )
        
        properties.forEach { it.afterChange { validate() } }
    }
    
    fun getErrorText() = when {
        !projectPathIsValid -> IdeBundle.message("new.dir.project.error.invalid")
        !baseInterpreterIsValid -> message("newProjectPanel.validation.noBaseInterpreter")
        !uvExecutablePathIsValid -> message("newProjectPanel.validation.invalidUVExecutable")
        !distributionNameIsValid -> message("newProjectPanel.validation.invalidDistributionName")
        else -> null
    }
    
}


private fun Row.projectNameInput(block: Cell<JBTextField>.() -> Unit) =
    textField().apply(block)


private fun Row.projectLocationInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFolderTextField().makeFlexible().apply(block)


private fun Row.initializeGitInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(PyBundle.message("new.project.git")).apply(block)


private fun Row.baseInterpreterInput(block: Cell<PySdkPathChoosingComboBox>.() -> Unit) =
    cell(PySdkPathChoosingComboBox()).makeFlexible().apply {
        component.addSystemWideInterpreters()
        block(this)
    }


private fun Row.uvExecutableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.distributionNameInput(block: Cell<JBTextField>.() -> Unit) =
    textField().apply(block)


private fun Panel.projectKindInputGroup() =
    buttonsGroup {
        row(message("newProjectPanel.settings.projectKind.label")) {
            radioButtonFor(ProjectKind.APP)
            radioButtonFor(ProjectKind.LIBRARY)
            radioButtonFor(ProjectKind.PACKAGED_APP)
        }
    }


private fun Row.createReadmeInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("newProjectPanel.settings.createReadme.label")).apply(block)


private fun Row.pinPythonInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("newProjectPanel.settings.pinPython.label")).apply(block)


@Suppress("DialogTitleCapitalization")
internal fun UVProjectSettingsStepPanel.makeComponent() = panel {
    row(PyBundle.message("new.project.name")) {
        projectNameInput { bindText(projectName) }
    }
    row(PyBundle.message("new.project.location")) {
        projectLocationInput {
            bindText(projectParentDirectory)
            
            projectLocationInput = component
        }
    }
    row("") {
        reactiveLabel(projectPathHint)
    }
    
    row("") {
        initializeGitInput { bindSelected(settings::initializeGit) }
    }
    
    panel {
        row(PySdkBundle.message("python.venv.base.label")) {
            topGap(TopGap.MEDIUM)
            
            // TODO: Switch to pythonInterpreterComboBox once 2024.3 is out
            baseInterpreterInput { component.childComponent.bind(baseSDKItem) }
        }
        
        row(message("newProjectPanel.settings.uvExecutable.label")) {
            uvExecutableInput {
                bindText(uvExecutable)
                
                uvExecutable.set(globalUVExecutable?.toString().orEmpty())
            }
        }
        row("") {
            reactiveLabel(uvExecutablePathHint)
        }
    }
    
    group(message("newProjectPanel.settings.groups.projectInitialization")) {
        row(message("newProjectPanel.settings.distributionName.label")) {
            distributionNameInput {
                bindText(settings::distributionName)
                bindText(distributionName)
                component.emptyText.bind(projectName)
            }
        }
        row("") {
            reactiveLabel(distributionNameHint)
        }
        
        projectKindInputGroup().bindSelected(settings::projectKind)
        
        row("") {
            createReadmeInput { bindSelected(settings::createReadme) }
            pinPythonInput { bindSelected(settings::pinPython) }
        }
    }
}
