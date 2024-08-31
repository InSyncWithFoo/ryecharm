package insyncwithfoo.ryecharm.ruff.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.CommandWithTimeout
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.globalRuffExecutable
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurable
import insyncwithfoo.ryecharm.configurations.ruff.globalRuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.findExecutableChild
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.binaryDirectory
import java.nio.file.Path


internal interface RuffCommand : CommandWithTimeout {
    
    override val configurable: Class<out PanelBasedConfigurable<*>>
        get() = RuffConfigurable::class.java
    
    override fun getTimeout(project: Project?) = when {
        project?.isDefault != false -> globalRuffConfigurations.timeouts[timeoutKey]
        else -> project.ruffConfigurations.timeouts[timeoutKey]
    }
    
}


internal class Ruff private constructor(
    val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun check(text: String, stdinFilename: Path?) =
        CheckCommand(executable, text, stdinFilename).build()
    
    fun format(text: String, stdinFilename: Path?, range: OneBasedRange? = null) =
        FormatCommand(executable, text, stdinFilename, range).build()
    
    fun clean(path: Path) =
        CleanCommand(executable).also { it.workingDirectory = path }
    
    fun config(option: String? = null) =
        ConfigCommand(executable, option).build()
    
    fun linter() =
        LinterCommand(executable).build()
    
    fun rule(code: String) =
        RuleCommand(executable, code).build()
    
    fun version() =
        VersionCommand(executable).build()
    
    fun optimizeImports(text: String, stdinFilename: Path?) =
        OptimizeImportsCommand(executable, text, stdinFilename).build()
    
    fun <T : Command> T.build() = this.apply {
        setWorkingDirectory()
    }
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalRuffExecutable?.let { Ruff(it, project = null, workingDirectory = null) }
            else -> project.ruffExecutable?.let { Ruff(it, project, project.path) }
        }
    }
    
}


internal fun Ruff.Companion.detectExecutable() =
    Rye.binaryDirectory?.findExecutableChild("ruff") ?: findExecutableInPath("ruff")


internal val Project.ruff: Ruff?
    get() = Ruff.create(this)
