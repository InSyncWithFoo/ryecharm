package insyncwithfoo.ryecharm.redknot.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.path


internal class RedKnotServerConnectionProvider(commands: List<String>, workingDirectory: String?) :
    ProcessStreamConnectionProvider(commands, workingDirectory)
{
    
    companion object {
        fun create(project: Project): RedKnotServerConnectionProvider {
            val executable = project.redKnotExecutable!!
            
            val fragments: List<String> = listOf(executable.toString(), "server")
            val workingDirectory = project.path?.toString()
            
            return RedKnotServerConnectionProvider(fragments, workingDirectory)
        }
    }
    
}
