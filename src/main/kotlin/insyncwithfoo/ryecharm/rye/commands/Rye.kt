package insyncwithfoo.ryecharm.rye.commands

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalRyeExecutable
import insyncwithfoo.ryecharm.configurations.ryeExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.toNullIfNotExists
import java.nio.file.Path
import kotlin.io.path.div


internal interface RyeCommand


internal class Rye private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    fun config() =
        ConfigCommand().build(CommandArguments("--show-path"))
    
    fun show() =
        ShowCommand().build()
    
    override fun CommandArguments.withGlobalOptions() = this
    
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


private val binarySubdirectoryName: String
    get() = when {
        SystemInfo.isWindows -> "Scripts"
        else -> "bin"
    }


internal val Rye.Companion.binaryDirectory: Path?
    get() = homeDirectory?.let { it / "self" / binarySubdirectoryName }


internal fun Rye.Companion.detectExecutable() = findExecutableInPath("rye")


internal val Project.rye: Rye?
    get() = Rye.create(this)
