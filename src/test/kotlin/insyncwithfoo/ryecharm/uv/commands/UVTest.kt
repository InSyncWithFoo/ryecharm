package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
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
        
        assertEquals(listOf("init"), command.subcommands)
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
    fun `test pip tree`() {
        val `package` = randomPEP508Name()
        val (inverted, showVersionSpecifiers, showLatestVersions, dedupe) = listOf(boolean, boolean, boolean, boolean)
        val depth = (0..10000).random()
        
        val command = uv.pipTree(`package`, inverted, showVersionSpecifiers, showLatestVersions, dedupe, depth)
        
        val arguments = command.arguments
        
        assertEquals(listOf("pip", "tree"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertTrue(arguments include listOf("--package", `package`))
        assertTrue(arguments include listOf("--depth", depth.toString()))
        
        if (inverted) {
            assertContains(arguments, "--invert")
        }
        
        if (showVersionSpecifiers) {
            assertContains(arguments, "--show-version-specifiers")
        }
        
        if (showLatestVersions) {
            assertContains(arguments, "--outdated")
        }
        
        if (!dedupe) {
            assertContains(arguments, "--no-dedupe")
        }
    }
    
    @Test
    fun `test version`() {
        val command = uv.version()
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test install group`() {
        val name = randomPEP508Name()
        val command = uv.installGroup(name)
        
        assertEquals(listOf("sync"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertTrue(command.arguments include listOf("--group", name))
    }
    
    @Test
    fun `test install all groups`() {
        val command = uv.installAllGroups()
        
        assertEquals(listOf("sync"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(command.arguments, "--all-groups")
    }
    
    @Test
    fun `test install extra`() {
        val name = randomPEP508Name()
        val command = uv.installExtra(name)
        
        assertEquals(listOf("sync"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertTrue(command.arguments include listOf("--extra", name))
    }
    
    @Test
    fun `test install all extras`() {
        val command = uv.installAllExtras()
        
        assertEquals(listOf("sync"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(command.arguments, "--all-extras")
    }
    
}
