package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.getEventualDelegate
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.testDataPath
import org.junit.Test


internal class SynchronizeProjectTest : PlatformTestCase() {
    
    private val intention = SynchronizeProject()
    
    override fun getTestDataPath() = this::class.testDataPath
    
    @Test
    fun `test startInWriteAction`() {
        assertEquals(true, intention.startInWriteAction())
    }
    
    @Test
    fun `test generatePreview`() = fileBasedTest("pyproject.toml") {
        assertEquals(IntentionPreviewInfo.EMPTY, intention.generatePreview(project, editor, file))
    }
    
    @Test
    fun `test isAvailable`() = fileBasedTest("pyproject.toml") {
        assertEquals(true, intention.isAvailable(project, editor, file))
    }
    
    @Test
    fun `test availability`() = fileBasedTest("pyproject.toml") {
        val hint = message("intentions.uv.sync.familyName")
        val availableIntention = fixture.filterAvailableIntentions(hint)
            .map { it.getEventualDelegate() }
            .single()
        
        assertSame(SynchronizeProject::class, availableIntention::class)
    }
    
}
