package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.platform.lsp.tests.waitUntilFileOpenedByLspServer
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import insyncwithfoo.ryecharm.LanguageServerTestCase
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.openFile


internal class LanguageServerTest : LanguageServerTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    fun `test diagnostics - python file`() {
        val file = fixture.configureByFile("F401.py")
        
        thisLogger().warn(file.virtualFile.toString())
        thisLogger().warn(file.virtualFile.toNioPath().toFile().exists().toString())
        
        project.openFile(file.virtualFile)
        waitUntilFileOpenedByLspServer(project, file.virtualFile)
        
        fixture.checkLspHighlighting()
    }
    
}
