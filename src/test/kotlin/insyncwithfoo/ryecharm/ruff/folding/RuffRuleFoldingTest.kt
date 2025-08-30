package insyncwithfoo.ryecharm.ruff.folding

import insyncwithfoo.ryecharm.PlatformTestCase
import org.junit.Test


internal class RuffRuleFoldingTest : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        project.getCodeToNameMapOrTriggerRetrieving()
    }
    
    @Test
    fun `test pyproject toml`() = doTest("pyproject.toml")
    
    @Test
    fun `test ruff toml`() = doTest("ruff.toml")
    
    @Test
    fun `test noqa`() = doTest("foo.py")
    
    private fun doTest(filePath: String) = fileBasedTest(filePath) {
        fixture.testFoldingWithCollapseStatus("$testDataPath/$filePath")
    }
    
}
