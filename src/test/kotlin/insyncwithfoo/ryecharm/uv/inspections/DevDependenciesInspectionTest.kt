package insyncwithfoo.ryecharm.uv.inspections

import insyncwithfoo.ryecharm.PlatformTestCase
import kotlin.test.Test


internal class DevDependenciesInspectionTest : PlatformTestCase() {
    
    @Test
    fun `test basic - pyproject`() = doTest("basic/pyproject.toml")
    
    @Test
    fun `test basic - uv`() = doTest("basic/uv.toml") 
    
    private fun doTest(subpath: String) = fileBasedTest(subpath) {
        fixture.enableInspections(DevDependenciesInspection())
        fixture.checkHighlighting()
    }
    
}
