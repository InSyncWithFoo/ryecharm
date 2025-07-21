package insyncwithfoo.ryecharm

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.guessProjectDir
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl


internal abstract class LanguageServerTestCase : PlatformTestCase() {
    
    override fun createTempDirTestFixture() = TempDirTestFixtureImpl()
    
    protected fun languageServerDiagnosticTest(filePath: String) =
        fileBasedTest(filePath) {
            thisLogger().warn(project.guessProjectDir()?.toString())
            thisLogger().warn(project.basePath)
            thisLogger().warn(file.toString())
            
            fixture.checkLspHighlighting()
        }
    
}
