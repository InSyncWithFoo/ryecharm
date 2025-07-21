package insyncwithfoo.ryecharm.ty.lsp

import insyncwithfoo.ryecharm.LanguageServerTestCase
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeTYConfigurations
import insyncwithfoo.ryecharm.configurations.changeTYOverrides
import insyncwithfoo.ryecharm.configurations.ty.RunningMode


internal class LanguageServerTest : LanguageServerTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        project.changeTYConfigurations {
            runningMode = RunningMode.LSP
            project.changeTYOverrides { add(::runningMode.name) }
        }
    }
    
    fun `test diagnostics`() = languageServerDiagnosticTest("invalid-assignment.py")
    
}
