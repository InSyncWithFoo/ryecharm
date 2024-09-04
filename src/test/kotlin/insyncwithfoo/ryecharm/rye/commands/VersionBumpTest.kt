package insyncwithfoo.ryecharm.rye.commands

import org.junit.Assert.assertEquals
import org.junit.Test


internal class VersionBumpTest {
    
    @Test
    fun `test value`() {
        assertEquals("major", VersionBumpType.MAJOR.toString())
        assertEquals("minor", VersionBumpType.MINOR.toString())
        assertEquals("patch", VersionBumpType.PATCH.toString())
    }
    
}
