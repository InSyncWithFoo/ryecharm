package insyncwithfoo.ryecharm.configurations.ty

import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message
import kotlinx.serialization.SerialName


internal enum class RunningMode(override val label: String) : Labeled {
    DISABLED(message("configurations.ty.runningMode.disabled")),
    LSP4IJ(message("configurations.ty.runningMode.lsp4ij")),
    LSP(message("configurations.ty.runningMode.lsp"));
}


@Suppress("unused")
internal enum class DiagnosticMode(override val label: String) : Labeled {
    @SerialName("openFilesOnly") OPEN_FILES_ONLY(message("configurations.ty.diagnosticMode.openFilesOnly")),
    @SerialName("workspace") WORKSPACE(message("configurations.ty.diagnosticMode.workspace"));
}


internal class TYConfigurations : DisplayableState(), Copyable {
    var executable by string(null)
    var runningMode by enum(RunningMode.DISABLED)
    
    var diagnostics by property(true)
    var diagnosticMode by enum(DiagnosticMode.OPEN_FILES_ONLY)
    
    var inlayHints by property(true)
    var inlayHintsVariableTypes by property(true)
    var inlayHintsCallArgumentNames by property(true)
    
    var experimentalRename by property(false)
    var experimentalAutoImport by property(false)
}


internal class TYOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
