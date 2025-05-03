package insyncwithfoo.ryecharm.ty.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalTyExecutable
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import java.nio.file.Path


internal interface TyCommand


internal class Ty private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    override fun CommandArguments.withGlobalOptions() = this
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalTyExecutable?.let { Ty(it, project = null, workingDirectory = null) }
            else -> project.tyExecutable?.let { Ty(it, project, project.path) }
        }
    }
    
}


internal fun Ty.Companion.detectExecutable() =
    findExecutableInPath("ty")


internal val Project.ty: Ty?
    get() = Ty.create(this)
