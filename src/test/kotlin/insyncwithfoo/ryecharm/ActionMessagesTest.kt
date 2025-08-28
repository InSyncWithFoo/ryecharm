package insyncwithfoo.ryecharm

import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnAction
import org.junit.Test
import kotlin.test.assertTrue


internal class ActionMessagesTest {
    
    private val notRegistered = listOf(
        "insyncwithfoo.ryecharm.others.installers.InstallDependencies",
        "insyncwithfoo.ryecharm.ruff.hierarchy.ImportGraphBrowser.ChangeViewTypeAction"
    )
    
    private val Class<*>.isExempted: Boolean
        get() = canonicalName in notRegistered || this.isSubtypeOf<NotificationAction>()
    
    private val Class<*>.toolName: String?
        get() {
            val relativeName = canonicalName.removePrefix("${RyeCharm.ID}.")
            val fragments = relativeName.split(".")
            
            return fragments.first().lowercase().takeIf {
                it in listOf("ruff", "rye", "uv", "ty")
            }
        }
    
    @Test
    fun `test - all actions have text and description`() {
        `package`.getSubtypesOf<AnAction>().filterNot { it.isExempted }.forEach {
            val id = it.canonicalName
            
            assertHasMessage("action.$id.text")
            assertHasMessage("action.$id.description")
        }
    }
    
    @Test
    fun `test - tool action texts are correspondingly prefixed`() {
        `package`.getSubtypesOf<AnAction>().filterNot { it.isExempted }.forEach {
            val (id, tool) = Pair(it.canonicalName, it.toolName)
            val text = message("action.$id.text")
            
            if (tool == null) {
                return@forEach
            }
            
            assertTrue(
                text.lowercase().startsWith("${it.toolName}: "),
                "`$id`'s text does not start with corresponding tool name"
            )
        }
    }
    
}
