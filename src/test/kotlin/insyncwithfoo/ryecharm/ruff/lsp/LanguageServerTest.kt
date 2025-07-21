package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerDescriptor
import com.jetbrains.rd.util.threading.coroutines.RdCoroutineScope.Companion.override
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.canBeLintedByRuff
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.lspServerManager
import org.junit.Test


internal class LanguageServerTest : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    @Test
    fun `test diagnostics - python file`() = languageServerDiagnosticTest("F401.py")
    
    @Test
    fun `test diagnostics - pyproject toml`() = languageServerDiagnosticTest("pyproject.toml")
    
}
