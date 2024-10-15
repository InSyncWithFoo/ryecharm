package insyncwithfoo.ryecharm.common.inspection

import insyncwithfoo.ryecharm.PlatformTestCase
import org.junit.Test


internal class DependencyGroupNameInspectionTest : PlatformTestCase() {
    
    @Test
    fun `test non-existent group`() = doTest("nonExistentGroup")
    
    @Test
    fun `test invalid group name`() = doTest("invalidGroupName")
    
    @Test
    fun `test normalization`() = doTest("normalizedComparison")
    
    @Test
    fun `test invalid key`() = doTest("invalidKey")
    
    private fun doTest(subdrectory: String) = fileBasedTest("$subdrectory/pyproject.toml") {
        fixture.enableInspections(DependencyGroupNameInspection())
        fixture.checkHighlighting()
    }
    
}
