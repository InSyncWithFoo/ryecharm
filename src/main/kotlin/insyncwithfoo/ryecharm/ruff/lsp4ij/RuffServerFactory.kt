package insyncwithfoo.ryecharm.ruff.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable


internal const val SERVER_ID = "ryecharm/ruff"


internal class RuffServerFactory : LanguageServerFactory, LanguageServerEnablementSupport {
    
    override fun isEnabled(project: Project): Boolean {
        val configurations = project.ruffConfigurations
        val runningModeIsLSP4IJ = configurations.runningMode == RunningMode.LSP4IJ
        val executable = project.ruffExecutable
        
        return runningModeIsLSP4IJ && executable != null
    }
    
    override fun setEnabled(enabled: Boolean, project: Project) {
        project.changeRuffConfigurations {
            runningMode = RunningMode.NO_LINTING
            
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return RuffServerConnectionProvider.create(project)
    }
    
    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return RuffServerClient(project)
    }
    
}
