package insyncwithfoo.ryecharm

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.DumbAware
import org.junit.Test
import org.reflections.Reflections
import kotlin.jvm.java
import kotlin.test.assertTrue


private inline fun <reified C> Reflections.getSubtypesOf() =
    getSubTypesOf(C::class.java)


internal class DumbAwareActionsTest {
    
    private val `package`: Reflections
        get() = Reflections(RyeCharm.ID)
    
    private fun assertDumbAware(`class`: Class<*>) {
        assertTrue(
            DumbAware::class.java.isAssignableFrom(`class`),
            "${`class`.simpleName} is not `DumbAware`"
        )
    }
    
    @Test
    fun `test - all actions are dumbaware`() {
        `package`.getSubtypesOf<AnAction>().forEach {
            assertDumbAware(it)
        }
    }
    
    @Test
    fun `test - all intentions are dumbaware`() {
        `package`.getSubtypesOf<IntentionAction>().forEach {
            if (!it.isInterface) {
                assertDumbAware(it)
            }
        }
    }
    
}
