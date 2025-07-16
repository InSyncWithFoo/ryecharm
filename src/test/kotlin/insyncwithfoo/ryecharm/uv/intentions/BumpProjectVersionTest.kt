package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.getEventualDelegate
import insyncwithfoo.ryecharm.message
import org.junit.Test


internal class BumpProjectVersionTest : PlatformTestCase() {
    
    private val intention: IntentionAction
        get() {
            val hint = message("intentions.uv.bumpProjectVersion.familyName")
            
            return fixture.filterAvailableIntentions(hint)
                .map { it.getEventualDelegate() }
                .single()
        }
    
    @Test
    fun `test intention`() = fileBasedTest("pyproject.toml") {
        val intention = intention
        
        assertInstanceOf(intention, BumpProjectVersion::class.java)
        assertTrue(intention.isAvailable(project, editor, file))
        
        assertEquals(IntentionPreviewInfo.EMPTY, intention.generatePreview(project, editor, file))
        assertEquals(true, intention.startInWriteAction())
    }
    
}
