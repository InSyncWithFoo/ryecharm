package insyncwithfoo.ryecharm.others.scriptmetadata

import insyncwithfoo.ryecharm.LanguageInjectionTestCase
import org.junit.Test
import org.toml.lang.psi.TomlFile
import kotlin.test.assertContains


internal class InlineScriptMetadataInjectorTest : LanguageInjectionTestCase() {
    
    @Test
    fun `test empty line`() = fileBasedTest("empty_line.py") {
        val fragment = fragments.single()
        
        assertTrue("\n#\n" in hostFile.text)
        assertInstanceOf(fragment, TomlFile::class.java)
    }
    
    @Test
    fun `test empty line trailing whitespace`() = fileBasedTest("empty_line_trailing_whitespace.py") {
        val fragment = fragments.single()
        
        assertTrue("\n# \n" in hostFile.text)
        assertInstanceOf(fragment, TomlFile::class.java)
    }
    
    @Test
    fun `test invalid line`() = fileBasedTest("invalid_line.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test missing start`() = fileBasedTest("missing_start.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test missing end`() = fileBasedTest("missing_end.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test start with no prefix`() = fileBasedTest("start_with_no_prefix.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test start with multiple paddings`() = fileBasedTest("start_with_multiple_paddings.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test start with no paddings`() = fileBasedTest("start_with_no_paddings.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test start with trailing space`() = fileBasedTest("start_with_trailing_space.py") {
        assertTrue(hostFile.text.startsWith("# /// script \n"))
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test end with no prefix`() = fileBasedTest("end_with_no_prefix.py") {
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test end with trailing space`() = fileBasedTest("end_with_trailing_space.py") {
        assertTrue(hostFile.text.endsWith("\n# /// \n"))
        assertFileDoesNotHaveInjections()
    }
    
    @Test
    fun `test multiple valid blocks`() = fileBasedTest("multiple_valid_blocks.py") {
        val fragment = fragments.single()
        
        assertInstanceOf(fragment, TomlFile::class.java)
        assertContains(fragment.text, "first = true")
    }
    
    @Test
    fun `test greedy end`() = fileBasedTest("greedy_end.py") {
        val fragment = fragments.single()
        val lines = fragment.text.split("\n")
        
        assertEquals(3, lines.size)
        assertEquals("", lines.last())
        
        assertContains(lines, "///")
    }
    
    @Test
    fun `test backtracking end`() = fileBasedTest("backtracking_end.py") {
        val fragment = fragments.single()
        val lines = fragment.text.split("\n")
        
        assertEquals(3, lines.size)
        assertEquals("", lines.last())
        
        assertContains(lines, "///")
    }
    
    @Test
    fun `test stub file`() = fileBasedTest("stub_file.pyi") {
        assertFileDoesNotHaveInjections()
    }
    
}
