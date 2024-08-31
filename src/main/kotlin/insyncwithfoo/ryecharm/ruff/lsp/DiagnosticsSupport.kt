package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.customization.LspDiagnosticsSupport
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import org.eclipse.lsp4j.Diagnostic


private val Diagnostic.codeAsString: String?
    get() = code?.get() as String?


@Suppress("UnstableApiUsage")
internal class DiagnosticsSupport(project: Project) : LspDiagnosticsSupport() {
    
    private val tooltipFormat = project.ruffConfigurations.tooltipFormat
    
    override fun getTooltip(diagnostic: Diagnostic): String {
        val rule = diagnostic.codeAsString
        val message = diagnostic.message

        return tooltipFormat % Pair(message, rule)
    }
    
}
