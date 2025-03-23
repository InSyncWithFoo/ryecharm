package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.getEventualDelegate
import insyncwithfoo.ryecharm.message
import org.junit.Test


internal class EditScriptMetadataFragmentTest : PlatformTestCase() {
    
    private val intention = EditScriptMetadataFragment()
    
    @Test
    fun `test startInWriteAction`() {
        assertEquals(false, intention.startInWriteAction())
    }
    
    @Test
    fun `test generatePreview`() = fileBasedTest("basic.py") {
        assertEquals(IntentionPreviewInfo.EMPTY, intention.generatePreview(project, editor, file))
    }
    
    @Test
    fun `test isAvailable - block body`() = availabilityTest("block_body.py", true)
    
    @Test
    fun `test isAvailable - block start`() = availabilityTest("block_start.py", true)
    
    @Test
    fun `test isAvailable - block end`() = availabilityTest("block_end.py", true)
    
    @Test
    fun `test isAvailable - line start`() = availabilityTest("line_start.py", true)
    
    @Test
    fun `test isAvailable - before block`() = availabilityTest("before_block.py", false)
    
    @Test
    fun `test isAvailable - after block`() = availabilityTest("after_block.py", false)
    
    @Test(expected = AssertionError::class)
    fun `test isAvailable - embedded - 1`() = availabilityTest("embedded_1.py", false)
    
    @Test(expected = AssertionError::class)
    fun `test isAvailable - embedded - 2`() = availabilityTest("embedded_2.py", true)
    
    @Test
    fun `test offset - block start - 1`() = offsetTest("block_start_1.py", 0)
    
    @Test
    fun `test offset - block start - 2`() = offsetTest("block_start_2.py", 0)
    
    @Test
    fun `test offset - block start - 3`() = offsetTest("block_start_3.py", 0)
    
    @Test
    fun `test offset - block end - 1`() = offsetTest("block_end_1.py", 14)
    
    @Test
    fun `test offset - block end - 2`() = offsetTest("block_end_2.py", 14)
    
    @Test
    fun `test offset - block end - 3`() = offsetTest("block_end_3.py", 14)
    
    @Test
    fun `test offset - line start - 1`() = offsetTest("line_start_1.py", 0)
    
    @Test
    fun `test offset - line start - 2`() = offsetTest("line_start_2.py", 0)
    
    @Test
    fun `test offset - line start - 3`() = offsetTest("line_start_3.py", 25)
    
    @Test
    fun `test offset - line start - 4`() = offsetTest("line_start_4.py", 25)
    
    @Test
    fun `test offset - block body - 1`() = offsetTest("block_body_1.py", 24)
    
    @Test
    fun `test offset - block body - 2`() = offsetTest("block_body_2.py", 32)
    
    private fun availabilityTest(filePath: String, expected: Boolean) = fileBasedTest("availability/$filePath") {
        assertEquals(expected, intention.isAvailable(project, editor, file))
        
        if (expected) {
            val hint = message("intentions.main.editScriptMetadataFragment.familyName")
            val availableIntention = fixture.filterAvailableIntentions(hint)
                .map { it.getEventualDelegate() }
                .single()
            
            assertSame(EditScriptMetadataFragment::class, availableIntention::class)
        }
    }
    
    private fun offsetTest(filePath: String, expected: Int) = fileBasedTest("offset/$filePath") {
        val file = this.file as PyFile
        val actual = file.calculateCursorOffsetInFragment(editor)
        
        assertEquals(expected, actual)
    }
    
}
