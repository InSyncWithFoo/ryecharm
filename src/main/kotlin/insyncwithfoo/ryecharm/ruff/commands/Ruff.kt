package insyncwithfoo.ryecharm.ruff.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalRuffExecutable
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.findExecutableChild
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.OptionName
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelector
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.binaryDirectory
import java.nio.file.Path


internal interface RuffCommand


internal class Ruff private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun check(arguments: CommandArguments, stdin: String? = null) =
        CheckCommand().build(arguments, stdin)
    
    fun format(arguments: CommandArguments, stdin: String) =
        FormatCommand().build(arguments, stdin)
    
    fun clean() =
        CleanCommand().build()
    
    fun rule(arguments: CommandArguments) =
        RuleCommand().build(arguments)
    
    fun config(arguments: CommandArguments) =
        ConfigCommand().build(arguments)
    
    fun linter(arguments: CommandArguments) =
        LinterCommand().build(arguments)
    
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
    
    fun fix(text: String, stdinFilename: Path?, select: List<RuleSelector>?, unsafeFixes: Boolean): Command {
        val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
        
        if (select != null) {
            arguments["--select"] = select.joinToString(",")
        }
        
        if (unsafeFixes) {
            arguments += "--unsafe-fixes"
        }
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return FixCommand().build(arguments, text)
    }
    
    fun fixAll(text: String, stdinFilename: Path?, unsafeFixes: Boolean) =
        fix(text, stdinFilename, select = null, unsafeFixes)
    
    fun organizeImports(text: String, stdinFilename: Path?): Command {
        val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
        
        arguments["--select"] = "I"
        
        if (stdinFilename != null) {
            arguments["--stdin-filename"] = stdinFilename.toString()
        }
        
        return OrganizeImportsCommand().build(arguments, text)
    }
    
    fun showSettings(
        select: List<RuleSelector>? = null,
        isolated: Boolean = true,
        preview: Boolean = true
    ): Command {
        val arguments = CommandArguments("--show-settings")
        
        if (isolated) {
            arguments += "--isolated"
        }
        
        if (preview) {
            arguments += "--preview"
        }
        
        if (select != null) {
            arguments["--select"] = select.joinToString(",")
        }
        
        return ShowSettingsCommand().build(arguments)
    }
    
    override fun CommandArguments.withGlobalOptions() = this.apply {
        val configurations = project?.ruffConfigurations
        val configurationFile = configurations?.configurationFile
        
        if (configurationFile != null && "--isolated" !in this) {
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
    findExecutableInPath("ruff") ?: Rye.binaryDirectory?.findExecutableChild("ruff")


internal val Project.ruff: Ruff?
    get() = Ruff.create(this)
