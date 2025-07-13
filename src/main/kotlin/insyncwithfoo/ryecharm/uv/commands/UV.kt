package insyncwithfoo.ryecharm.uv.commands

import com.intellij.openapi.project.Project
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.configurations.globalUVExecutable
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.findExecutableChild
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.lastModified
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.homeDirectory
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries


internal typealias ProjectVersion = String


internal enum class ProjectKind(override val label: String) : Labeled {
    APP(message("newProjectPanel.settings.projectKind.app")),
    LIBRARY(message("newProjectPanel.settings.projectKind.library")),
    PACKAGED_APP(message("newProjectPanel.settings.projectKind.packagedApp"));
}


internal fun PythonPackageSpecification.toPEP508Format() =
    name + versionSpecs.orEmpty()


internal enum class VersionBumpType {
    MAJOR, MINOR, PATCH;
    
    override fun toString() = name.lowercase()
}


internal interface UVCommand


internal class UV private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun init(arguments: CommandArguments) =
        InitCommand().build(arguments)
    
    fun add(arguments: CommandArguments) =
        AddCommand().build(arguments)
    
    fun upgrade(arguments: CommandArguments) =
        UpgradeCommand().build(arguments)
    
    fun remove(arguments: CommandArguments) =
        RemoveCommand().build(arguments)
    
    fun sync() =
        SyncCommand().build()
    
    fun installDependencies(kind: String, arguments: CommandArguments) =
        InstallDependenciesCommand(kind).build(arguments)
    
    fun venv(baseInterpreter: Path, name: String? = null): Command {
        val arguments = CommandArguments("--python" to baseInterpreter.toString())
        
        if (name != null) {
            arguments += name
        }
        
        return VenvCommand().build(arguments)
    }
    
    fun version(arguments: CommandArguments) =
        VersionCommand().build(arguments)
    
    fun selfVersion(arguments: CommandArguments) =
        SelfVersionCommand().build(arguments)
    
    fun selfUpdate() =
        SelfUpdateCommand().build()
    
    fun pipCompile(packages: List<String>, noHeader: Boolean = true): Command {
        val arguments = CommandArguments("-")
        val stdin = packages.joinToString("\n")
        
        if (noHeader) {
            arguments += "--no-header"
        }
        
        return PipCompileCommand().build(arguments, stdin)
    }
    
    fun pipList(python: Path? = null): Command {
        val arguments = CommandArguments("--format", "json", "--quiet")
        
        if (python != null) {
            arguments["--python"] = python.toString()
        }
        
        return PipListCommand().build(arguments)
    }
    
    // TODO: `--prune`, `--strict` (?)
    fun pipTree(
        `package`: String,
        inverted: Boolean,
        showVersionSpecifiers: Boolean,
        showLatestVersions: Boolean,
        dedupe: Boolean,
        depth: Int,
        interpreter: Path?
    ): Command {
        val arguments = CommandArguments("--package" to `package`, "--depth" to depth.toString())
        
        if (inverted) {
            arguments += "--invert"
        }
        
        if (showVersionSpecifiers) {
            arguments += "--show-version-specifiers"
        }
        
        if (showLatestVersions) {
            arguments += "--outdated"
        }
        
        if (!dedupe) {
            arguments += "--no-dedupe"
        }
        
        if (interpreter != null) {
            arguments["--python"] = interpreter.toString()
        }
        
        return PipTreeCommand().build(arguments)
    }
    
    override fun CommandArguments.withGlobalOptions() = this.apply {
        val configurations = project?.uvConfigurations
        val configurationFile = configurations?.configurationFile
        
        if (configurationFile != null) {
            this["--config-file"] = configurationFile
        }
    }
    
    companion object {
        
        fun create(project: Project): UV? = when {
            project.isDefault -> globalUVExecutable?.let { UV(it, project = null, workingDirectory = null) }
            else -> project.uvExecutable?.let { UV(it, project, project.path) }
        }
        
        fun create(executable: Path, workingDirectory: Path?) =
            UV(executable, project = null, workingDirectory)
        
    }
    
}


private val Rye.Companion.uvSubdirectory: Path?
    get() = homeDirectory?.let { it / "uv" }?.takeIf { it.isDirectory() }


private val Rye.Companion.latestUVSubdirectory: Path?
    get() = uvSubdirectory?.listDirectoryEntries("*")?.maxByOrNull { it.lastModified }


internal fun UV.Companion.detectExecutable() =
    findExecutableInPath("uv") ?: Rye.latestUVSubdirectory?.findExecutableChild("uv")


internal val Project.uv: UV?
    get() = UV.create(this)
