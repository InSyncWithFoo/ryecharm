package insyncwithfoo.ryecharm

import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory


internal abstract class LanguageServerTestCase : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        fixture.testDataPath = this.testDataPath
    }
    
    final override fun createMyFixture(): CodeInsightTestFixture? {
        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        
        val name = """${this::class.java.name}.$name"""
        val projectBuilder = fixtureFactory.createFixtureBuilder(name)
        
        return fixtureFactory.createCodeInsightFixture(projectBuilder.fixture)
    }
    
    protected fun languageServerDiagnosticTest(filePath: String) =
        fileBasedTest(filePath) {
            fixture.checkLspHighlighting()
        }
    
}
