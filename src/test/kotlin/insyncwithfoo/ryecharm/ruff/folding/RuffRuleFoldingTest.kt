package insyncwithfoo.ryecharm.ruff.folding

import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import org.junit.Test


internal class RuffRuleFoldingTest : PlatformTestCase() {
    
    private var codeToNameMap: Map<RuleCode, RuleName>? = null
    
    override fun setUp() {
        super.setUp()
        
        codeToNameMap = project.getCodeToNameMapOrTriggerRetrieving()
    }
    
    @Test
    fun `test map`() {
        assertNotNull(codeToNameMap)
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
