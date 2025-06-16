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
    
    fun init(
        name: String?,
        kind: ProjectKind,
        createReadme: Boolean,
        pinPython: Boolean,
        baseInterpreter: Path
    ): Command {
        val arguments = CommandArguments("--no-workspace")
        
        arguments["--python"] = baseInterpreter.toString()
        
        if (name != null) {
            arguments["--name"] = name
        }
        
        when (kind) {
            ProjectKind.APP -> arguments += "--app"
            ProjectKind.LIBRARY -> arguments += "--lib"
            ProjectKind.PACKAGED_APP -> arguments += listOf("--app", "--package")
        }
        
        if (!createReadme) {
            arguments += "--no-readme"
        }
        
        if (!pinPython) {
            arguments += "--no-pin-python"
        }
        
        return InitCommand().build(arguments)
    }
    
    fun add(target: PythonPackageSpecification) =
        AddCommand().build(CommandArguments(target.toPEP508Format()))
    
    fun upgrade(target: PythonPackageSpecification) =
        UpgradeCommand().build(CommandArguments(target.toPEP508Format(), "--upgrade"))
    
    fun remove(target: String) =
        RemoveCommand().build(CommandArguments(target))
    
    fun sync() =
        SyncCommand().build()
    
    fun installGroup(name: String): Command {
        val kind = message("progresses.command.uv.installDependencies.kind.group", name)
        
        return InstallDependenciesCommand(kind).build(CommandArguments("--group" to name))
    }
    
    fun installAllGroups(): Command {
        val kind = message("progresses.command.uv.installDependencies.kind.allGroups")
        
        return InstallDependenciesCommand(kind).build(CommandArguments("--all-groups"))
    }
    
    fun installExtra(name: String): Command {
        val kind = message("progresses.command.uv.installDependencies.kind.extra", name)
        
        return InstallDependenciesCommand(kind).build(CommandArguments("--extra" to name))
    }
    
    fun installAllExtras(): Command {
        val kind = message("progresses.command.uv.installDependencies.kind.allExtras")
        
        return InstallDependenciesCommand(kind).build(CommandArguments("--all-extras"))
    }
    
    fun venv(baseInterpreter: Path, name: String? = null): Command {
        val arguments = CommandArguments("--python" to baseInterpreter.toString())
        
        if (name != null) {
            arguments += name
        }
        
        return VenvCommand().build(arguments)
    }
    
    fun version() =
        VersionCommand().build(CommandArguments("--short"))
    
    fun version(bumpType: VersionBumpType): Command {
        val arguments = CommandArguments("--short")
        
        arguments["--bump"] = bumpType.toString()
        
        return VersionCommand().build(arguments)
    }
    
    fun version(newVersion: ProjectVersion) =
        VersionCommand().build(CommandArguments("--short", newVersion))
    
    fun selfVersion() =
        SelfVersionCommand().build()
    
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
