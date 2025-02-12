package insyncwithfoo.ryecharm.redknot.commands

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.CommandFactory
import insyncwithfoo.ryecharm.configurations.globalRedKnotExecutable
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.findExecutableInPath
import insyncwithfoo.ryecharm.path
import java.nio.file.Path


internal interface RedKnotCommand


internal class RedKnot private constructor(
    override val executable: Path,
    private val project: Project?,
    override val workingDirectory: Path?
) : CommandFactory() {
    
    override fun CommandArguments.withGlobalOptions() = this
    
    companion object {
        fun create(project: Project) = when {
            project.isDefault -> globalRedKnotExecutable?.let { RedKnot(it, project = null, workingDirectory = null) }
            else -> project.redKnotExecutable?.let { RedKnot(it, project, project.path) }
        }
    }
    
}


internal fun RedKnot.Companion.detectExecutable() =
    findExecutableInPath("red_knot")


internal val Project.redKnot: RedKnot?
    get() = RedKnot.create(this)
