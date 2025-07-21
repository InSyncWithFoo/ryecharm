package insyncwithfoo.ryecharm.ty.lsp

import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.ModuleFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl


@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
internal class LanguageServerTest2 : CodeInsightFixtureTestCase<ModuleFixtureBuilder<ModuleFixture>>() {
    
    override fun setUp() {
        super.setUp()
        (myFixture as CodeInsightTestFixtureImpl).canChangeDocumentDuringHighlighting(true)
        myFixture.testDataPath = "src/test/testData/ty/lsp/LanguageServerTest"
    }
    
    fun `test invalid assignment`() {
        myFixture.configureByFile("invalid-assignment.py")
        myFixture.checkLspHighlighting()
    }
    
}
