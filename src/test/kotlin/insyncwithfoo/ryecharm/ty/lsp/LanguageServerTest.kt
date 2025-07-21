package insyncwithfoo.ryecharm.ty.lsp

import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeTYConfigurations
import insyncwithfoo.ryecharm.configurations.changeTYOverrides
import insyncwithfoo.ryecharm.configurations.ty.RunningMode
import org.junit.Test


internal class LanguageServerTest : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        project.changeTYConfigurations {
            runningMode = RunningMode.LSP
            project.changeTYOverrides { add(::runningMode.name) }
        }
    }
    
    @Test
    fun `test diagnostics`() = languageServerDiagnosticTest("invalid-assignment")
    
}
