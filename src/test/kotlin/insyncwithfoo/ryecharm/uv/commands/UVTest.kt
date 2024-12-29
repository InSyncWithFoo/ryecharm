package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import insyncwithfoo.ryecharm.uv.generator.ProjectKind
import org.junit.Test
import kotlin.test.assertContains


internal class UVTest : CommandFactoryTest() {
    
    private lateinit var uv: UV
    
    override fun setUp() {
        super.setUp()
        
        uv = project.uv!!
    }
    
    private fun randomPEP508Name(): String {
        val first = listOf(lowercase, uppercase).random()
        val last = listOf(lowercase, uppercase).random()
        
        val middle = buildString(0..20) {
            listOf(lowercase, uppercase, digit, '.', '_', '-').random()
        }
        
        return first + middle + last
    }
    
    @Test
    fun `test init`() {
        val randomNameCharacter = { listOf(lowercase, uppercase, digit, "-", ".", "_").random() }
        
        val name = buildString(5..20, randomNameCharacter).orRandomlyNull()
        val kind = ProjectKind.entries.random()
        val createReadme = boolean
        val pinPython = boolean
        val baseInterpreter = randomPath()
        
        val command = uv.init(name, kind, createReadme, pinPython, baseInterpreter)
        val arguments = command.arguments
        
        assertEquals("init", command.subcommand)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "--no-workspace")
        assertTrue(arguments include listOf("--python", baseInterpreter.toString()))
        
        when (kind) {
            ProjectKind.APP -> {
                assertContains(arguments, "--app")
                assertTrue("--package" !in arguments)
                assertTrue("--no-package" !in arguments)
            }
            ProjectKind.LIBRARY -> {
                assertContains(arguments, "--lib")
                assertTrue("--package" !in arguments)
                assertTrue("--no-package" !in arguments)
            }
            ProjectKind.PACKAGED_APP -> {
                assertContains(arguments, "--app")
                assertContains(arguments, "--package")
                assertTrue("--no-package" !in arguments)
            }
        }
        
        if (name != null) {
            assertTrue(arguments include listOf("--name", name))
        }
        
        if (!createReadme) {
            assertContains(arguments, "--no-readme")
        }
        
        if (!pinPython) {
            assertContains(arguments, "--no-pin-python")
        }
    }
    
    @Test
    fun `test version`() {
        val command = uv.version()
        
        assertEquals("version", command.subcommand)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test install group`() {
        val name = randomPEP508Name()
        val command = uv.installGroup(name)
        
        assertEquals("sync", command.subcommand)
        assertTrue(command.arguments include listOf("--group", name))
    }
    
    @Test
    fun `test install all groups`() {
        val command = uv.installAllGroups()
        
        assertEquals("sync", command.subcommand)
        assertContains(command.arguments, "--all-groups")
    }
    
    @Test
    fun `test install extra`() {
        val name = randomPEP508Name()
        val command = uv.installExtra(name)
        
        assertEquals("sync", command.subcommand)
        assertTrue(command.arguments include listOf("--extra", name))
    }
    
    @Test
    fun `test install all extras`() {
        val command = uv.installAllExtras()
        
        assertEquals("sync", command.subcommand)
        assertContains(command.arguments, "--all-extras")
    }
    
}
