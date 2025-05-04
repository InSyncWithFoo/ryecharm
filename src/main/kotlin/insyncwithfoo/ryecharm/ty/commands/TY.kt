package insyncwithfoo.ryecharm.ty.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalTYExecutable
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import java.nio.file.Path


internal interface TYCommand


internal class TY private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    override fun CommandArguments.withGlobalOptions() = this
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalTYExecutable?.let { TY(it, project = null, workingDirectory = null) }
            else -> project.tyExecutable?.let { TY(it, project, project.path) }
        }
    }
    
}


internal fun TY.Companion.detectExecutable() =
    findExecutableInPath("ty")


internal val Project.ty: TY?
    get() = TY.create(this)
