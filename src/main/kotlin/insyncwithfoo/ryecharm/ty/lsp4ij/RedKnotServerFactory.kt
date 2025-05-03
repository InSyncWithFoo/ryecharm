package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRedKnotConfigurations
import insyncwithfoo.ryecharm.configurations.changeRedKnotOverrides
import insyncwithfoo.ryecharm.configurations.redKnotExecutable
import insyncwithfoo.ryecharm.configurations.ty.RunningMode
import insyncwithfoo.ryecharm.configurations.ty.redKnotConfigurations


/**
 * The ID of the LSP4IJ server,
 * as registered in `lsp4ij.xml`.
 */
internal const val SERVER_ID = "${RyeCharm.ID}.ty"


internal class RedKnotServerFactory : LanguageServerFactory, LanguageServerEnablementSupport {
    
    override fun isEnabled(project: Project): Boolean {
        val configurations = project.redKnotConfigurations
        val runningModeIsLSP4IJ = configurations.runningMode == RunningMode.LSP4IJ
        val executable = project.redKnotExecutable
        
        return runningModeIsLSP4IJ && executable != null
    }
    
    override fun setEnabled(enabled: Boolean, project: Project) {
        project.changeRedKnotConfigurations {
            runningMode = when {
                enabled -> RunningMode.LSP4IJ
                else -> RunningMode.DISABLED
            }
            
            project.changeRedKnotOverrides { add(::runningMode.name) }
        }
    }
    
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return RedKnotServerConnectionProvider.create(project)
    }
    
    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return RedKnotServerClient(project)
    }
    
}
