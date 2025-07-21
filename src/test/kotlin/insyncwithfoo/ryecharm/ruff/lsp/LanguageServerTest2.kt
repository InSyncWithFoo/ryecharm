package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.ModuleFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl


@TestDataPath($$"$CONTENT_ROOT/testData")
internal class LanguageServerTest2 : CodeInsightFixtureTestCase<ModuleFixtureBuilder<ModuleFixture>>() {
    
    override fun setUp() {
        super.setUp()
        (myFixture as CodeInsightTestFixtureImpl).canChangeDocumentDuringHighlighting(true)
        myFixture.testDataPath = "testData/ruff/lsp/LanguageServerTest"
    }
    
    fun `test F401`() {
        myFixture.configureByFile("F401.py")
        myFixture.checkLspHighlighting()
    }
    
    fun `test pyproject toml`() {
        myFixture.configureByFile("pyproject.toml")
        myFixture.checkLspHighlighting()
    }
    
}
