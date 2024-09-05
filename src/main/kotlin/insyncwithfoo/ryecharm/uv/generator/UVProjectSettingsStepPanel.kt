package insyncwithfoo.ryecharm.uv.generator

import com.intellij.facet.ui.ValidationResult
import com.intellij.ide.IdeBundle
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.bind
import com.intellij.openapi.observable.util.bindBooleanStorage
import com.intellij.openapi.observable.util.joinCanonicalPath
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.PyBundle
import com.jetbrains.python.PySdkBundle
import com.jetbrains.python.sdk.add.PySdkComboBoxItem
import com.jetbrains.python.sdk.add.PySdkPathChoosingComboBox
import com.jetbrains.python.sdk.add.addInterpretersAsync
import com.jetbrains.python.sdk.detectSystemWideSdks
import insyncwithfoo.ryecharm.applyReturningComponent
import insyncwithfoo.ryecharm.configurations.globalUVExecutable
import insyncwithfoo.ryecharm.isNonEmptyDirectory
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
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


private fun PySdkPathChoosingComboBox.addInterpreters(obtainer: () -> List<Sdk>) {
    addInterpretersAsync(this, obtainer)
}


internal fun PySdkPathChoosingComboBox.addSystemWideInterpreters() {
    addInterpreters { detectSystemWideSdks(module = null, existingSdks = emptyList()) }
}


private fun Panel.rowWithTopGap(label: String, init: Row.() -> Unit) {
    row(label, init).topGap(TopGap.MEDIUM)
}


private fun ValidationResult.toInfo() =
    errorMessage?.let { ValidationInfo(it) }


private fun <T> GraphProperty<T>.dependsOn(vararg parents: ObservableProperty<*>, update: () -> T) {
    parents.forEach { parent ->
        dependsOn(parent, update)
    }
}


internal class UVProjectSettingsStepPanel(val projectGenerator: UVProjectGenerator) {
    
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
    
    private val projectPathIsValid = propertyGraph.property(false).apply {
        dependsOn(projectPathHint) {
            projectPathHint.get() == PyBundle.message("new.project.location.hint", projectPath)
        }
    }
    
    val baseInterpreter = propertyGraph.property<PySdkComboBoxItem?>(null)
    
    private val baseInterpreterIsValid = propertyGraph.property(false).apply {
        dependsOn(baseInterpreter) {
            baseInterpreter.get() != null
        }
    }
    
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
    
    private val uvExecutablePathIsValid = propertyGraph.property(false).apply {
        dependsOn(uvExecutablePathHint) {
            uvExecutablePathHint.get() == message("newProjectPanel.hint.fileFound")
        }
    }
    
    val initializeGit = propertyGraph.property(false)
        .bindBooleanStorage("PyCharm.NewProject.Git")
    
    lateinit var projectLocationInput: TextFieldWithBrowseButton
    lateinit var baseInterpreterInput: PySdkPathChoosingComboBox
    
    val projectLocation: String
        get() = FileUtil.expandUserHome(projectParentDirectory.joinCanonicalPath(projectName).get())
            
    val venvCreator: VenvCreator
        get() = VenvCreator(
            uvExecutable = uvExecutable.get().toPathOrNull()!!,
            projectPath = projectPath!!,
            baseSdk = baseInterpreterInput.selectedSdk!!
        )
    
    fun setNewProjectName(nextProjectName: File) {
        projectName.set(nextProjectName.nameWithoutExtension)
        projectParentDirectory.set(nextProjectName.parent)
    }
    
    fun registerValidator(validate: () -> Unit) {
        val properties = listOf(projectName, projectParentDirectory, baseInterpreter, uvExecutable)
        
        properties.forEach { it.afterChange { validate() } }
    }
    
    fun getErrorText() = when {
        !projectPathIsValid.get() -> IdeBundle.message("new.dir.project.error.invalid")
        !baseInterpreterIsValid.get() -> message("newProjectPanel.validation.noBaseInterpreter")
        !uvExecutablePathIsValid.get() -> message("newProjectPanel.validation.invalidUVExecutable")
        else -> null
    }
    
}


internal fun UVProjectSettingsStepPanel.makeComponent() = panel {
    row(PyBundle.message("new.project.name")) {
        textField().apply {
            validationOnInput { projectGenerator.validate(projectLocation).toInfo() }
            bindText(projectName)
        }
    }
    row(PyBundle.message("new.project.location")) {
        projectLocationInput = singleFolderTextField().applyReturningComponent {
            makeFlexible()
            bindText(projectParentDirectory)
        }
    }
    row("") {
        reactiveLabel(projectPathHint)
    }
    
    row("") {
        checkBox(PyBundle.message("new.project.git")).bindSelected(initializeGit)
    }
    
    panel {
        rowWithTopGap(PySdkBundle.message("python.venv.base.label")) {
            // TODO: Switch to pythonInterpreterComboBox once 2024.3 is out
            baseInterpreterInput = cell(PySdkPathChoosingComboBox()).applyReturningComponent {
                makeFlexible()
                component.addSystemWideInterpreters()
                component.childComponent.bind(baseInterpreter)
            }
        }
        
        row(message("newProjectPanel.settings.uvExecutable.label")) {
            singleFileTextField().applyReturningComponent {
                makeFlexible()
                bindText(uvExecutable)
                
                uvExecutable.set(globalUVExecutable?.toString().orEmpty())
            }
        }
        row("") {
            reactiveLabel(uvExecutablePathHint)
        }
    }
}
