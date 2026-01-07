package insyncwithfoo.ryecharm.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Builder
import insyncwithfoo.ryecharm.configurations.ty.DiagnosticMode
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.invoke


internal data class InlayHints(
    var variableTypes: Boolean = true,
    var callArgumentNames: Boolean = true
) : Builder


internal data class Experimental(
    var rename: Boolean = false,
    var autoImport: Boolean = false
) : Builder


internal data class WorkspaceConfiguration(
    var diagnosticMode: DiagnosticMode? = null,
    
    val inlayHints: InlayHints = InlayHints(),
    val experimental: Experimental = Experimental()
)


internal fun Project.createWorkspaceConfigurationObject() = WorkspaceConfiguration().apply {
    val configurations = tyConfigurations
    
    // TODO: DiagnosticMode members are serialized as ordinals, despite SerialName.
    // diagnosticMode = configurations.diagnosticMode
    
    inlayHints {
        variableTypes = configurations.inlayHintsVariableTypes
        callArgumentNames = configurations.inlayHintsCallArgumentNames
    }
    experimental {
        rename = configurations.experimentalRename
        autoImport = configurations.experimentalAutoImport
    }
}
