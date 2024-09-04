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
        
        assertEquals("-", arguments.last())
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
        
        val command = ruff.format(text, path, range)
        val arguments = command.arguments
        
        assertEquals("format", command.subcommand)
        assertEquals(text, command.stdin)
        assertEquals(projectPath, command.workingDirectory)
        
        assertEquals("-", arguments.last())
        assertContains(arguments, "--quiet")
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
        
        if (range != null) {
            assertTrue(arguments include listOf("--range", range.toString()))
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
            val linterPrefix = buildString((1..5).random()) { lowercase }
            val ruleNumber = buildString((3..6).random()) { digit }
            
            append(linterPrefix)
            append(ruleNumber)
        }
        val command = ruff.rule(code)
        
        assertEquals("rule", command.subcommand)
        assertEquals(listOf(code), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test config`() {
        val option = buildString((10..30).random()) {
            listOf(lowercase, '.', '-').random()
        }
        val command = ruff.config(option)
        
        assertEquals("config", command.subcommand)
        assertEquals(listOf(option, "--output-format", "json"), command.arguments)
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
        
        assertEquals("-", arguments.last())
        assertContains(arguments, "--fix")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--select", "I,F401"))
        
        if (path != null) {
            assertTrue(arguments include listOf("--stdin-filename", path.toString()))
        }
    }
    
}
