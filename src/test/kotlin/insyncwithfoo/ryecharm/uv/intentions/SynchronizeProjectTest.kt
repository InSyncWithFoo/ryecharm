package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.getEventualDelegate
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.testDataPath


internal class SynchronizeProjectTest : PlatformTestCase() {
    
    private val intention = SynchronizeProject()
    
    override fun getTestDataPath() = this::class.testDataPath
    
    fun `test startInWriteAction`() {
        assertEquals(true, intention.startInWriteAction())
    }
    
    fun `test generatePreview`() {
        fixture.configureByFile("pyproject.toml")
        
        assertEquals(IntentionPreviewInfo.EMPTY, intention.generatePreview(project, editor, file))
    }
    
    fun `test isAvailable`() {
        fixture.configureByFile("pyproject.toml")
        
        assertEquals(true, intention.isAvailable(project, editor, file))
    }
    
    fun `test availability`() {
        fixture.configureByFile("pyproject.toml")
        
        val hint = message("intentions.uv.sync.familyName")
        val availableIntention = fixture.filterAvailableIntentions(hint)
            .map { it.getEventualDelegate() }
            .single()
        
        assertSame(SynchronizeProject::class, availableIntention::class)
    }
    
}
