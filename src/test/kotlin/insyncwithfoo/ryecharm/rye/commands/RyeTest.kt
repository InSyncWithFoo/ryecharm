package insyncwithfoo.ryecharm.rye.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import org.junit.Test


internal class RyeTest : CommandFactoryTest() {
    
    private lateinit var rye: Rye
    
    override fun setUp() {
        super.setUp()
        
        rye = project.rye!!
    }
    
    @Test
    fun `test config`() {
        val command = rye.config()
        
        assertEquals(listOf("config"), command.subcommands)
        assertEquals(listOf("--show-path"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test show`() {
        val command = rye.show()
        
        assertEquals(listOf("show"), command.subcommands)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
}
