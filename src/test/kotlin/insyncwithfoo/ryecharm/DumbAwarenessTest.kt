package insyncwithfoo.ryecharm

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.PossiblyDumbAware
import org.junit.Test


internal class DumbAwarenessTest {
    
    @Test
    fun `test - all possiblydumbaware are dumbaware`() {
        `package`.getSubtypesOf<PossiblyDumbAware>().forEach {
            assertSubtypeOf<DumbAware>(it)
        }
    }
    
    @Test
    fun `test - all dumbaware are possiblydumbaware`() {
        `package`.getSubtypesOf<DumbAware>().forEach {
            assertSubtypeOf<PossiblyDumbAware>(it)
        }
    }
    
}
