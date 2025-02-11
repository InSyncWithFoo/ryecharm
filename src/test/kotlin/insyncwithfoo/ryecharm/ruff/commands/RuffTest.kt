package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import org.junit.Test
import kotlin.test.assertContains


internal class RuffTest : CommandFactoryTest() {
    
    private lateinit var ruff: Ruff
    
    override fun setUp() {
        super.setUp()
        
        ruff = project.ruff!!
    }
    
    @Test
    fun `test check`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        
        val command = ruff.check(text, path)
        val arguments = command.arguments
        
        assertEquals("check", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        assertContains(arguments, "--no-fix")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--output-format", "json"))
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
    }
    
    @Test
    fun `test format`() {
        val randomInteger = { (0..10000).random() }
        val randomPinpoint = { randomInteger() to randomInteger() }
        
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        val range = OneBasedRange(randomPinpoint(), randomPinpoint()).orRandomlyNull()
        val quiet = boolean
        
        val command = ruff.format(text, path, range)
        val arguments = command.arguments
        
        assertEquals("format", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
        
        if (range != null) {
            assertTrue(arguments include listOf("--range", range.toString()))
        }
        
        if (quiet) {
            assertContains(arguments, "--quiet")
        }
    }
    
    @Test
    fun `test clean`() {
        val path = randomPath()
        val command = ruff.clean(path)
        
        assertEquals("clean", command.subcommand)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(path, command.workingDirectory)
    }
    
    @Test
    fun `test rule`() {
        val code = buildString {
            val linterPrefix = buildString(1..5) { lowercase }
            val ruleNumber = buildString(3..6) { digit }
            
            append(linterPrefix)
            append(ruleNumber)
        }
        val command = ruff.rule(code)
        
        assertEquals("rule", command.subcommand)
        assertEquals(listOf(code), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test allRules`() {
        val command = ruff.allRules()
        val arguments = command.arguments
        
        assertEquals("rule", command.subcommand)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "--all")
        
        assertTrue(arguments include listOf("--output-format", "json"))
    }
    
    @Test
    fun `test config`() {
        val option = buildString(10..30) {
            listOf(lowercase, '.', '-').random()
        }
        val command = ruff.config(option)
        
        assertEquals("config", command.subcommand)
        assertEquals(listOf(option, "--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test allConfig`() {
        val command = ruff.allConfig()
        
        assertEquals("config", command.subcommand)
        assertEquals(listOf("--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test linter`() {
        val command = ruff.linter()
        
        assertEquals("linter", command.subcommand)
        assertEquals(listOf("--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test version`() {
        val command = ruff.version()
        
        assertEquals("version", command.subcommand)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test optimizeImports`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        
        val command = ruff.optimizeImports(text, path)
        val arguments = command.arguments
        
        assertEquals("check", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        assertContains(arguments, "--fix")
        assertContains(arguments, "--fix-only")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--select", "I,F401"))
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
    }
    
    @Test
    fun `test fix`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        val select = List((0..10).random()) { randomText() }
        val unsafeFixes = boolean
        
        val command = ruff.fix(text, path, select, unsafeFixes)
        val arguments = command.arguments
        
        assertEquals("check", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        assertContains(arguments, "--fix")
        assertContains(arguments, "--fix-only")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--select", select.joinToString(",")))
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
        
        if (unsafeFixes) {
            assertContains(arguments, "--unsafe-fixes")
        }
    }
    
    @Test
    fun `test fixAll`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        val unsafeFixes = boolean
        
        val command = ruff.fixAll(text, path, unsafeFixes = unsafeFixes)
        val arguments = command.arguments
        
        assertEquals("check", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        assertContains(arguments, "--fix")
        assertContains(arguments, "--fix-only")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue("--select" !in arguments)
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
        
        if (unsafeFixes) {
            assertContains(arguments, "--unsafe-fixes")
        }
    }
    
    @Test
    fun `test organizeImports`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        
        val command = ruff.organizeImports(text, path)
        val arguments = command.arguments
        
        assertEquals("check", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "-")
        assertContains(arguments, "--fix")
        assertContains(arguments, "--fix-only")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--select", "I"))
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
    }
    
}
