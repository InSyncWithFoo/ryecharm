package insyncwithfoo.ryecharm.rye.commands

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.CommandWithTimeout
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.globalRyeExecutable
import insyncwithfoo.ryecharm.configurations.rye.RyeConfigurable
import insyncwithfoo.ryecharm.configurations.rye.globalRyeConfigurations
import insyncwithfoo.ryecharm.configurations.rye.ryeConfigurations
import insyncwithfoo.ryecharm.configurations.ryeExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.toNullIfNotExists
import java.nio.file.Path
import kotlin.io.path.div


private val binarySubdirectoryName: String
    get() = when {
        SystemInfo.isWindows -> "Scripts"
        else -> "bin"
    }


internal interface RyeCommand : CommandWithTimeout {
    
    override val configurable: Class<out PanelBasedConfigurable<*>>
        get() = RyeConfigurable::class.java
    
    override fun getTimeout(project: Project?) = when {
        project?.isDefault != false -> globalRyeConfigurations.timeouts[timeoutKey]
        else -> project.ryeConfigurations.timeouts[timeoutKey]
    }
    
}


internal class Rye private constructor(
    val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    private val projectBound: Boolean
        get() = project != null
    
    fun config() =
        ConfigCommand(executable).setWorkingDirectory()
    
    fun show() =
        ShowCommand(executable).setWorkingDirectory()
    
    fun version() =
        VersionCommand(executable, listOf()).setWorkingDirectory()
    
    fun version(bumpType: VersionBumpType) =
        VersionCommand(executable, listOf("--bump", bumpType.toString())).setWorkingDirectory()
    
    fun version(newVersion: ProjectVersion) =
        VersionCommand(executable, listOf(newVersion)).setWorkingDirectory()
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalRyeExecutable?.let { Rye(it, project = null, workingDirectory = null) }
            else -> project.ryeExecutable?.let { Rye(it, project, project.path) }
        }
    }
    
}


internal val Rye.Companion.homeDirectory: Path?
    get() {
        val executable = detectExecutable() ?: return null
        val shimsSubdirectory = executable.parent
        val homeDirectory = shimsSubdirectory?.parent
        
        return homeDirectory?.toNullIfNotExists()
    }

internal val Rye.Companion.binaryDirectory: Path?
    get() = homeDirectory?.let { it / "self" / binarySubdirectoryName }


internal fun Rye.Companion.detectExecutable() = findExecutableInPath("rye")


internal val Project.rye: Rye?
    get() = Rye.create(this)
