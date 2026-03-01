package insyncwithfoo.ryecharm.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Builder
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.invoke


internal data class InlayHints(
    var variableTypes: Boolean = true,
    var callArgumentNames: Boolean = true
) : Builder


internal data class Completions(
    var autoImport: Boolean = true
) : Builder


internal data class WorkspaceConfiguration(
    var configurationFile: String? = null,
    var disableLanguageServices: Boolean = false,
    var diagnosticMode: String? = null,
    var showSyntaxErrors: Boolean = false,
    
    val inlayHints: InlayHints = InlayHints(),
    val completions: Completions = Completions()
)


internal fun Project.createWorkspaceConfigurationObject() = WorkspaceConfiguration().apply {
    val configurations = tyConfigurations
    
    configurationFile = configurations.configurationFile
    disableLanguageServices = !configurations.enableLanguageServices
    diagnosticMode = configurations.diagnosticMode.toString()
    showSyntaxErrors = configurations.showSyntaxErrors
    
    inlayHints {
        variableTypes = configurations.inlayHintsVariableTypes
        callArgumentNames = configurations.inlayHintsCallArgumentNames
    }
    completions {
        autoImport = configurations.completionsAutoImport
    }
}
