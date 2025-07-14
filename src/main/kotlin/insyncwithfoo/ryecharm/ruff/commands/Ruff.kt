package insyncwithfoo.ryecharm.ruff.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalRuffExecutable
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.findExecutableChild
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
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
    
    fun clean() =
        CleanCommand().build()
    
    fun config(arguments: CommandArguments) =
        ConfigCommand().build(arguments)
    
    fun format(arguments: CommandArguments, stdin: String) =
        FormatCommand().build(arguments, stdin)
    
    fun linter(arguments: CommandArguments) =
        LinterCommand().build(arguments)
    
    fun rule(arguments: CommandArguments) =
        RuleCommand().build(arguments)
    
    fun version() =
        VersionCommand().build()
    
    fun fix(arguments: CommandArguments, stdin: String) =
        FixCommand().build(arguments, stdin)
    
    fun optimizeImports(arguments: CommandArguments, stdin: String) =
        OptimizeImportsCommand().build(arguments, stdin)
    
    fun organizeImports(arguments: CommandArguments, stdin: String) =
        OrganizeImportsCommand().build(arguments, stdin)
    
    fun showSettings(arguments: CommandArguments) =
        ShowSettingsCommand().build(arguments)
    
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
