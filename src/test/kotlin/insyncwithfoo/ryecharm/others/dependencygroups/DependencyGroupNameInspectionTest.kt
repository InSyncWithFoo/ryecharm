package insyncwithfoo.ryecharm.others.dependencygroups

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
    
    @Test
    fun `test duplicate groups`() = doTest("duplicateGroups")
    
    @Test
    fun `test circular group`() = doTest("circularGroup")
    
    private fun doTest(subdrectory: String) = fileBasedTest("$subdrectory/pyproject.toml") {
        fixture.enableInspections(DependencyGroupNameInspection())
        fixture.checkHighlighting()
    }
    
}
