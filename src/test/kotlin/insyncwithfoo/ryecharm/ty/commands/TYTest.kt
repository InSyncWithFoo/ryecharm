package insyncwithfoo.ryecharm.ty.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import org.junit.Test


internal class TYTest : CommandFactoryTest(TYCommand::class.java) {
    
    private lateinit var ty: TY
    
    override fun setUp() {
        super.setUp()
        
        ty = project.ty!!
    }

    @Test
    fun `test command classes`() {
        commandClassTest<VersionCommand>(listOf("version"))
    }
    
    @Test
    fun `test version`() =
        commandTest<VersionCommand>(ty.version())
    
}
