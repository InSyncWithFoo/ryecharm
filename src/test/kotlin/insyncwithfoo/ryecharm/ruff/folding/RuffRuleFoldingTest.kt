package insyncwithfoo.ryecharm.ruff.folding

import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import junit.framework.AssertionFailedError
import org.junit.Test


internal class RuffRuleFoldingTest : PlatformTestCase() {
    
    private var codeToNameMap: Map<RuleCode, RuleName>? = null
    
    override fun setUp() {
        super.setUp()
        
        codeToNameMap = project.getCodeToNameMapOrTriggerRetrieving()
    }
    
    @Test(expected = AssertionFailedError::class)
    fun `test map`() {
        assertNotNull(codeToNameMap)
    }
    
    @Test(expected = AssertionError::class)
    fun `test pyproject toml`() = doTest("pyproject.toml")
    
    @Test(expected = AssertionError::class)
    fun `test ruff toml`() = doTest("ruff.toml")
    
    // TODO: In 2026.1, ranges are expanded by default. Why?
    @Test(expected = AssertionError::class)
    fun `test noqa`() = doTest("foo.py")
    
    private fun doTest(filePath: String) = fileBasedTest(filePath) {
        fixture.testFoldingWithCollapseStatus("$testDataPath/$filePath")
    }
    
}
