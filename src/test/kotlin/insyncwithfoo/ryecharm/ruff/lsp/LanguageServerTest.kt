package insyncwithfoo.ryecharm.ruff.lsp

import insyncwithfoo.ryecharm.LanguageServerTestCase
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode


internal class LanguageServerTest : LanguageServerTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    fun `test diagnostics - python file`() = languageServerDiagnosticTest("F401.py")
    
    fun `test diagnostics - pyproject toml`() = languageServerDiagnosticTest("pyproject.toml")
    
}
