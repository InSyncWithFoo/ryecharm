package insyncwithfoo.ryecharm.common.injections

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.testDataPath
import org.junit.Test
import org.toml.lang.psi.TomlFile
import kotlin.test.assertContains


internal class InlineScriptMetadataInjectorTest : PlatformTestCase() {
    
    private val injectedLanguageManager: InjectedLanguageManager
        get() = InjectedLanguageManager.getInstance(project)
    
    private val hostFile: PsiFile
        get() = injectedLanguageManager.getTopLevelFile(file)
    
    private val PsiElement.injectedElements: List<PsiElement>
        get() = injectedLanguageManager.getInjectedPsiFiles(this)?.map { it.first } ?: emptyList()
    
    private val topLevelFragments: List<PsiElement>
        get() = hostFile.children.flatMap { it.injectedElements }
            .toSet().toList()
    
    override fun getTestDataPath() = this::class.testDataPath
    
    @Test
    fun `test empty line`() = fileBasedTest("empty_line.py") {
        assertTrue("\n#\n" in hostFile.text)
        assertFileHasInjection()
    }
    
    @Test
    fun `test empty line trailing whitespace`() = fileBasedTest("empty_line_trailing_whitespace.py") {
        assertTrue("\n# \n" in hostFile.text)
        assertFileHasInjection()
    }
    
    @Test
    fun `test invalid line`() = fileBasedTest("invalid_line.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test missing start`() = fileBasedTest("missing_start.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test missing end`() = fileBasedTest("missing_end.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test start with no prefix`() = fileBasedTest("start_with_no_prefix.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test start with multiple paddings`() = fileBasedTest("start_with_multiple_paddings.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test start with no paddings`() = fileBasedTest("start_with_no_paddings.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test start with trailing space`() = fileBasedTest("start_with_trailing_space.py") {
        assertTrue(hostFile.text.startsWith("# /// script \n"))
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test end with no prefix`() = fileBasedTest("end_with_no_prefix.py") {
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test end with trailing space`() = fileBasedTest("end_with_trailing_space.py") {
        assertTrue(hostFile.text.endsWith("\n# /// \n"))
        assertFileDoesNotHaveInjection()
    }
    
    @Test
    fun `test multiple valid blocks`() = fileBasedTest("multiple_valid_blocks.py") {
        val fragments = topLevelFragments
        
        assertFileHasInjection()
        assertEquals(1, fragments.size)
        
        assertContains(fragments.single().text, "first = true")
    }
    
    @Test
    fun `test greedy end`() = fileBasedTest("greedy_end.py") {
        val fragment = topLevelFragments.single()
        val lines = fragment.text.split("\n")
        
        assertEquals(3, lines.size)
        assertEquals("", lines.last())
        
        assertContains(lines, "///")
    }
    
    @Test
    fun `test backtracking end`() = fileBasedTest("backtracking_end.py") {
        val fragment = topLevelFragments.single()
        val lines = fragment.text.split("\n")
        
        assertEquals(3, lines.size)
        assertEquals("", lines.last())
        
        assertContains(lines, "///")
    }
    
    @Test
    fun `test stub file`() = fileBasedTest("stub_file.pyi") {
        assertFileDoesNotHaveInjection()
    }
    
    private fun assertFileHasInjection() {
        assertNotSame(file, hostFile)
        assertInstanceOf(file, TomlFile::class.java)
    }
    
    private fun assertFileDoesNotHaveInjection() {
        assertSame(file, hostFile)
    }
    
}
