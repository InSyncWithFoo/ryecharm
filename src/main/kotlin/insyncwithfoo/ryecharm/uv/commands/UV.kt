package insyncwithfoo.ryecharm.uv.commands

import com.intellij.openapi.project.Project
import com.jetbrains.python.packaging.common.PythonPackageSpecification
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
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.listDirectoryEntries


internal interface UVCommand : CommandWithTimeout {
    
    override val configurable: Class<out PanelBasedConfigurable<*>>
        get() = UVConfigurable::class.java
    
    override fun getTimeout(project: Project?) = when {
        project?.isDefault != false -> globalUVConfigurations.timeouts[timeoutKey]
        else -> project.uvConfigurations.timeouts[timeoutKey]
    }
    
}


internal class UV private constructor(
    val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    private val projectBound: Boolean
        get() = project != null
    
    fun init() =
        InitCommand(executable).setWorkingDirectory()
    
    fun add(target: PythonPackageSpecification) =
        AddCommand(executable, target).setWorkingDirectory()
    
    fun upgrade(target: PythonPackageSpecification) =
        UpgradeCommand(executable, target).setWorkingDirectory()
    
    fun remove(target: String) =
        RemoveCommand(executable, target).setWorkingDirectory()
    
    fun sync() =
        SyncCommand(executable).setWorkingDirectory()
    
    fun venv(baseInterpreter: Path, name: String? = null) =
        VenvCommand(executable, name, baseInterpreter).setWorkingDirectory()
    
    fun version() =
        VersionCommand(executable).setWorkingDirectory()
    
    fun pipList() =
        PipListCommand(executable).setWorkingDirectory()
    
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
