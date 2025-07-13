package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import org.junit.Test
import kotlin.test.assertContains


internal class UVTest : CommandFactoryTest(UVCommand::class.java) {
    
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
    fun `test command classes`() {
        commandClassTest<InitCommand>(listOf("init"))
        commandClassTest<AddCommand>(listOf("add"))
        commandClassTest<RemoveCommand>(listOf("remove"))
        commandClassTest<UpgradeCommand>(listOf("add"))
        commandClassTest<SyncCommand>(listOf("sync"))
        commandClassTest<InstallDependenciesCommand>(listOf("sync"))
        commandClassTest<VenvCommand>(listOf("venv"))
        commandClassTest<VersionCommand>(listOf("version"))
        commandClassTest<SelfVersionCommand>(listOf("self", "version"))
        commandClassTest<SelfUpdateCommand>(listOf("self", "update"))
        commandClassTest<PipCompileCommand>(listOf("pip", "compile"))
        commandClassTest<PipListCommand>(listOf("pip", "list"))
        commandClassTest<PipTreeCommand>(listOf("pip", "tree"))
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
        val interpreter = randomPath().orRandomlyNull()
        
        val command = uv.pipTree(
            `package`,
            inverted,
            showVersionSpecifiers,
            showLatestVersions,
            dedupe,
            depth,
            interpreter
        )
        
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
        
        if (interpreter != null) {
            assertTrue(arguments include listOf("--python", interpreter.toString()))
        } else {
            assertTrue("--python" !in arguments)
        }
    }
    
    @Test
    fun `test self version 1 - text`() {
        val command = uv.selfVersion(json = false)
        
        commandTest<SelfVersionCommand>(command)
    }
    
    @Test
    fun `test self version 2 - json`() {
        val command = uv.selfVersion(json = true)
        
        commandTest<SelfVersionCommand>(command) {
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test self update`() {
        val command = uv.selfUpdate()
        
        commandTest<SelfUpdateCommand>(command)
    }
    
    @Test
    fun `test pip compile`() {
        val packages = buildList((0..10).random()) {
            add(randomPEP508Name())
        }
        val noHeader = boolean
        
        val command = uv.pipCompile(packages, noHeader)
        val arguments = command.arguments
        
        assertEquals(listOf("pip", "compile"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        assertEquals(packages.joinToString("\n"), command.stdin)
        
        assertContains(arguments, "-")
        
        if (noHeader) {
            assertContains(command.arguments, "--no-header")
        }
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
    
    @Test
    fun `test version - get`() {
        val command = uv.version()
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(command.arguments, "--short")
    }
    
    @Test
    fun `test version - bump`() {
        val bumpType = VersionBumpType.entries.random()
        val command = uv.version(bumpType)
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(command.arguments, "--short")
        assertTrue(command.arguments include listOf("--bump", bumpType.toString()))
    }
    
    @Test
    fun `test version - set`() {
        val newVersion = buildString(5..10) {
            listOf(lowercase, '.', '-').random()
        }
        val command = uv.version(newVersion)
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(command.arguments, "--short")
        assertContains(command.arguments, newVersion)
    }
    
}
