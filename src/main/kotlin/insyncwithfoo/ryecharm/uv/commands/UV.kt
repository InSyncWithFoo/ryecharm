package insyncwithfoo.ryecharm.uv.commands

import com.intellij.openapi.project.Project
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


internal enum class VersionBumpType(override val label: String) : Labeled {
    MAJOR(message("dialogs.bumpProjectVersion.major")),
    MINOR(message("dialogs.bumpProjectVersion.minor")),
    PATCH(message("dialogs.bumpProjectVersion.patch")),
    STABLE(message("dialogs.bumpProjectVersion.stable")),
    ALPHA(message("dialogs.bumpProjectVersion.alpha")),
    BETA(message("dialogs.bumpProjectVersion.beta")),
    RC(message("dialogs.bumpProjectVersion.rc")),
    POST(message("dialogs.bumpProjectVersion.post")),
    DEV(message("dialogs.bumpProjectVersion.dev"));
    
    override fun toString() = name.lowercase()
}


internal interface UVCommand


internal class UV private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun add(arguments: CommandArguments) =
        AddCommand().build(arguments)
    
    fun init(arguments: CommandArguments) =
        InitCommand().build(arguments)
    
    fun installDependencies(kind: String, arguments: CommandArguments) =
        InstallDependenciesCommand(kind).build(arguments)
    
    fun remove(arguments: CommandArguments) =
        RemoveCommand().build(arguments)
    
    fun sync() =
        SyncCommand().build()
    
    fun upgrade(arguments: CommandArguments) =
        UpgradeCommand().build(arguments)
    
    fun venv(arguments: CommandArguments) =
        VenvCommand().build(arguments)
    
    fun version(arguments: CommandArguments) =
        VersionCommand().build(arguments)
    
    fun pipCompile(arguments: CommandArguments, stdin: String) =
        PipCompileCommand().build(arguments, stdin)
    
    fun pipList(arguments: CommandArguments) =
        PipListCommand().build(arguments)
    
    fun pipTree(arguments: CommandArguments) =
        PipTreeCommand().build(arguments)
    
    fun selfUpdate() =
        SelfUpdateCommand().build()
    
    fun selfVersion(arguments: CommandArguments) =
        SelfVersionCommand().build(arguments)
    
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
