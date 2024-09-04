package insyncwithfoo.ryecharm.rye.intentions

import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.getEventualDelegate
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.rye.commands.VersionBumpType
import insyncwithfoo.ryecharm.testDataPath


internal class BumpProjectVersionTest : PlatformTestCase() {
    
    private lateinit var intentions: List<BumpProjectVersion>
    
    override fun getTestDataPath() = this::class.testDataPath
    
    override fun setUp() {
        super.setUp()
        
        intentions = listOf(
            BumpProjectMajorVersion(),
            BumpProjectMinorVersion(),
            BumpProjectPatchVersion()
        )
    }
    
    fun `test bumpType`() {
        val (major, minor, patch) = intentions
        
        assertEquals(VersionBumpType.MAJOR, major.bumpType)
        assertEquals(VersionBumpType.MINOR, minor.bumpType)
        assertEquals(VersionBumpType.PATCH, patch.bumpType)
    }
    
    fun `test startInWriteAction`() {
        intentions.forEach { intention ->
            assertEquals(true, intention.startInWriteAction())
        }
    }
    
    fun `test getFamilyName`() {
        val familyNames = intentions.mapTo(mutableSetOf()) { it.familyName }
        assertEquals(3, familyNames.size)
    }
    
    fun `test getText`() {
        intentions.forEach { intention ->
            val prefix = message("intentions.rye.bumpProjectVersion.familyName")
            
            assertEquals(intention.familyName, intention.text)
            assertEquals("$prefix: ${intention.bumpType}", intention.text)
        }
    }
    
    fun `test generatePreview`() {
        fixture.configureByFile("pyproject.toml")
        
        intentions.forEach { intention ->
            assertEquals(IntentionPreviewInfo.EMPTY, intention.generatePreview(project, editor, file))
        }
    }
    
    fun `test isAvailable`() {
        fixture.configureByFile("pyproject.toml")
        
        intentions.forEach { intention ->
            assertEquals(true, intention.isAvailable(project, editor, file))
        }
    }
    
    fun `test availability`() {
        fixture.configureByFile("pyproject.toml")
        
        val hint = message("intentions.rye.bumpProjectVersion.familyName")
        val availableIntentions = fixture.filterAvailableIntentions(hint)
            .map { it.getEventualDelegate() }
        
        (intentions zip availableIntentions).forEach { (expected, actual) ->
            assertSame(expected::class, actual::class)
        }
    }
    
}
