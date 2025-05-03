package insyncwithfoo.ryecharm.ty.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalRedKnotExecutable
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import java.nio.file.Path


internal interface RedKnotCommand


internal class Ty private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    override fun CommandArguments.withGlobalOptions() = this
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalRedKnotExecutable?.let { Ty(it, project = null, workingDirectory = null) }
            else -> project.redKnotExecutable?.let { Ty(it, project, project.path) }
        }
    }
    
}


internal fun Ty.Companion.detectExecutable() =
    findExecutableInPath("red_knot")


internal val Project.redKnot: Ty?
    get() = Ty.create(this)
