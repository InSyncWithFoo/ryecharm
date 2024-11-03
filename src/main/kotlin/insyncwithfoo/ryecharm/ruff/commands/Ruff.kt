package insyncwithfoo.ryecharm.ruff.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
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
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun check(text: String, stdinFilename: Path?): Command {
        val arguments = CommandArguments("--no-fix", "--exit-zero", "--quiet", "-")
        
        arguments["--output-format"] = "json"
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return CheckCommand().build(arguments, text)
    }
    
    fun format(text: String, stdinFilename: Path?, range: OneBasedRange? = null): Command {
        val arguments = CommandArguments("--quiet", "-")
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        if (range != null) {
            arguments["--range"] = range.toString()
        }
        
        return FormatCommand().build(arguments, text)
    }
    
    fun clean(path: Path) =
        CleanCommand().build().also { it.workingDirectory = path }
    
    fun rule(code: String) =
        RuleCommand().build(CommandArguments(code))
    
    fun config(option: String? = null): Command {
        val arguments = CommandArguments("--output-format" to "json")
        
        if (option != null) {
            arguments += option
        }
        
        return ConfigCommand().build(arguments)
    }
    
    fun linter() =
        LinterCommand().build(CommandArguments("--output-format" to "json"))
    
    fun version() =
        VersionCommand().build()
    
    fun optimizeImports(text: String, stdinFilename: Path?): Command {
        val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
        
        arguments["--select"] = "I,F401"
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return OptimizeImportsCommand().build(arguments, text)
    }
    
    fun fixAll(text: String, stdinFilename: Path?): Command {
        val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return FixAllCommand().build(arguments, text)
    }
    
    fun organizeImports(text: String, stdinFilename: Path?): Command {
        val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
        
        arguments["--select"] = "I"
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return OrganizeImportsCommand().build(arguments, text)
    }
    
    override fun CommandArguments.withGlobalOptions() = this.apply {
        val configurations = project?.ruffConfigurations
        val configurationFile = configurations?.configurationFile
        
        if (configurationFile != null) {
            this["--config"] = configurationFile
        }
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
