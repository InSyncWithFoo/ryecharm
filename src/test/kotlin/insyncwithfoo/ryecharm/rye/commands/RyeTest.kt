package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import org.junit.Test


internal class RyeTest : CommandFactoryTest(RyeCommand::class.java) {
    
    private lateinit var rye: Rye
    
    override fun setUp() {
        super.setUp()
        
        rye = project.rye!!
    }

    @Test
    fun `test command classes`() {
        commandClassTest<ConfigCommand>(listOf("config"))
        commandClassTest<ShowCommand>(listOf("show"))
    }
    
    @Test
    fun `test config`() {
        val command = rye.configDirectory()
        
        commandTest<ConfigCommand>(command) {
            assertArgumentsContain("--show-path")
        }
    }
    
    @Test
    fun `test show`() {
        val command = rye.show()
        
        commandTest<ShowCommand>(command)
    }
    
}
