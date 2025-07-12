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
        commandTest(rye.configDirectory(), listOf("config"), listOf("--show-path")) {}
    }
    
    @Test
    fun `test show`() {
        commandTest(rye.show(), listOf("show"), emptyList()) {}
    }
    
}
