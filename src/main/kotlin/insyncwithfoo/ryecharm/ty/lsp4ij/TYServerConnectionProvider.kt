package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import insyncwithfoo.ryecharm.configurations.tyExecutable
import insyncwithfoo.ryecharm.path


internal class TYServerConnectionProvider(commands: List<String>, workingDirectory: String?) :
    ProcessStreamConnectionProvider(commands, workingDirectory)
{
    
    companion object {
        fun create(project: Project): TYServerConnectionProvider {
            val executable = project.tyExecutable!!
            
            val fragments: List<String> = listOf(executable.toString(), "server")
            val workingDirectory = project.path?.toString()
            
            return TYServerConnectionProvider(fragments, workingDirectory)
        }
    }
    
}
