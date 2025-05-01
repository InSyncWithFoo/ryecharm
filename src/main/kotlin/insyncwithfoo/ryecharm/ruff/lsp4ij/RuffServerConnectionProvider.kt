package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.path


internal class RuffServerConnectionProvider(commands: List<String>, workingDirectory: String?) :
    ProcessStreamConnectionProvider(commands, workingDirectory)
{
    
    companion object {
        fun create(project: Project): RuffServerConnectionProvider {
            val executable = project.ruffExecutable!!
            
            val fragments: List<String> = listOf(executable.toString(), "server")
            val workingDirectory = project.path?.toString()
            
            return RuffServerConnectionProvider(fragments, workingDirectory)
        }
    }
    
}
