package insyncwithfoo.ryecharm

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.guessProjectDir
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.ModuleFixture


internal abstract class LanguageServerTestCase : CodeInsightFixtureTestCase<ModuleFixtureBuilder<ModuleFixture>>() {
    
    protected fun languageServerDiagnosticTest(filePath: String) {
        myFixture.configureByFile(filePath)
        
        thisLogger().warn(project.guessProjectDir()?.toString())
        thisLogger().warn(project.basePath)
        thisLogger().warn(file.virtualFile.toString())
        
        myFixture.checkLspHighlighting()
    }
    
}
