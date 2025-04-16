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
    
    @Test
    fun `test version - get`() {
        val command = rye.version()
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test version - bump`() {
        val bumpType = VersionBumpType.entries.random()
        val command = rye.version(bumpType)
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(listOf("--bump", bumpType.toString()), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test version - set`() {
        val newVersion = buildString(5..10) {
            listOf(lowercase, '.', '-').random()
        }
        val command = rye.version(newVersion)
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(listOf(newVersion), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
}
