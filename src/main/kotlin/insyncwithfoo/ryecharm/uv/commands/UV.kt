package insyncwithfoo.ryecharm.uv.commands

import com.intellij.openapi.project.Project
import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.CommandWithTimeout
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.globalUVExecutable
import insyncwithfoo.ryecharm.configurations.uv.UVConfigurable
import insyncwithfoo.ryecharm.configurations.uv.globalUVConfigurations
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.configurations.uvExecutable
import insyncwithfoo.ryecharm.findExecutableChild
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.lastModified
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.homeDirectory
import insyncwithfoo.ryecharm.uv.generator.ProjectKind
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.listDirectoryEntries


internal fun PythonPackageSpecification.toPEP508Format() =
    name + versionSpecs.orEmpty()


internal interface UVCommand : CommandWithTimeout {
    
    override val configurable: Class<out PanelBasedConfigurable<*>>
        get() = UVConfigurable::class.java
    
    override fun getTimeout(project: Project?) = when {
        project?.isDefault != false -> globalUVConfigurations.timeouts[timeoutKey]
        else -> project.uvConfigurations.timeouts[timeoutKey]
    }
    
}


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
    
    fun sync(group: String? = null): Command {
        val arguments = CommandArguments()
        
        if (group != null) {
            arguments["--group"] = group
        }
        
        return SyncCommand().build(arguments)
    }
    
    fun venv(baseInterpreter: Path, name: String? = null): Command {
        val arguments = CommandArguments("--python" to baseInterpreter.toString())
        
        if (name != null) {
            arguments += name
        }
        
        return VenvCommand().build(arguments)
    }
    
    fun version() =
        VersionCommand().build()
    
    // FIXME: This seems problematic
    fun pipList() =
        PipListCommand().build(CommandArguments("list", "--format", "json"))
    
    private fun Command.build(arguments: CommandArguments? = null) = this.apply {
        this.arguments = arguments?.withGlobalOptions()?.toList() ?: emptyList()
        
        setExecutableAndWorkingDirectory()
    }
    
    private fun CommandArguments.withGlobalOptions() = this.apply {
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
    get() = homeDirectory?.let { it / "uv" }


private val Rye.Companion.latestUVSubdirectory: Path?
    get() = uvSubdirectory?.listDirectoryEntries("*")?.maxByOrNull { it.lastModified }


internal fun UV.Companion.detectExecutable() =
    Rye.latestUVSubdirectory?.findExecutableChild("uv") ?: findExecutableInPath("uv")


internal val Project.uv: UV?
    get() = UV.create(this)
