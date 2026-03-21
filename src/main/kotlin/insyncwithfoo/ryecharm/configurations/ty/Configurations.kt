package insyncwithfoo.ryecharm.configurations.ty

import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal enum class RunningMode(override val label: String) : Labeled {
    DISABLED(message("configurations.ty.runningMode.disabled")),
    LSP4IJ(message("configurations.ty.runningMode.lsp4ij")),
    LSP(message("configurations.ty.runningMode.lsp"));
}


@Suppress("unused")
internal enum class DiagnosticMode(override val label: String) : Labeled {
    OFF(message("configurations.ty.diagnosticMode.off")),
    OPEN_FILES_ONLY(message("configurations.ty.diagnosticMode.openFilesOnly")),
    WORKSPACE(message("configurations.ty.diagnosticMode.workspace"));
    
    override fun toString() = when (this) {
        OFF -> "off"
        OPEN_FILES_ONLY -> "openFilesOnly"
        WORKSPACE -> "workspace"
    }
}


@Suppress("unused")
internal enum class LogLevel(override val label: String) : Labeled {
    TRACE(message("configurations.ty.logLevel.trace")),
    DEBUG(message("configurations.ty.logLevel.debug")),
    INFO(message("configurations.ty.logLevel.info")),
    WARN(message("configurations.ty.logLevel.warn")),
    ERROR(message("configurations.ty.logLevel.error"));
    
    override fun toString() = name.lowercase()
}


internal class TYConfigurations : DisplayableState(), Copyable {
    var executable by string(null)
    var configurationFile by string(null)
    
    var runningMode by enum(RunningMode.DISABLED)
    
    var enableLanguageServices by property(true)
    
    var diagnostics by property(true)
    var showSyntaxErrors by property(false)
    var diagnosticMode by enum(DiagnosticMode.OPEN_FILES_ONLY)
    
    var inlayHints by property(true)
    var inlayHintsVariableTypes by property(true)
    var inlayHintsCallArgumentNames by property(true)
    
    var completions by property(true)
    var completionsAutoImport by property(true)
    
    var logLevel by enum(LogLevel.INFO)
    var logFile by string(null)
}


internal class TYOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
