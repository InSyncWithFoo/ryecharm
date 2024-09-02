package insyncwithfoo.ryecharm.ruff

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations


internal interface Builder


internal operator fun <B : Builder> B.invoke(block: B.() -> Unit) {
    this.apply(block)
}


internal data class DisableRuleComment(
    var enable: Boolean = true
) : Builder


internal data class FixViolation(
    var enable: Boolean = true
) : Builder


internal data class CodeAction(
    var disableRuleComment: DisableRuleComment = DisableRuleComment(),
    var fixViolation: FixViolation = FixViolation()
) : Builder


internal data class Lint(
    var enable: Boolean = true
) : Builder


internal data class InitializationOptions(
    var configuration: String? = null,
    var fixAll: Boolean = true,
    var organizeImports: Boolean = true,
    var showSyntaxErrors: Boolean = true,
    var logLevel: String = "info",
    var logFile: String? = null,
    
    val codeAction: CodeAction = CodeAction(),
    val lint: Lint = Lint()
)


internal fun Project.createInitializationOptionsObject() = InitializationOptions().apply {
    val configurations = ruffConfigurations
    
    configuration = configurations.configurationFile
    fixAll = configurations.fixAll
    organizeImports = configurations.organizeImports
    showSyntaxErrors = configurations.showSyntaxErrors
    logLevel = configurations.logLevel.toString()
    logFile = configurations.logFile
    
    codeAction {
        disableRuleComment { enable = configurations.disableRuleComment }
        fixViolation { enable = configurations.fixViolation }
    }
    
    lint {
        enable = configurations.linting
        showSyntaxErrors = configurations.showSyntaxErrors
    }
}
