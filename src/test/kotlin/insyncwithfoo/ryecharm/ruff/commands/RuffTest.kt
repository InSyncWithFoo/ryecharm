package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import org.junit.Test
import kotlin.test.assertContains


internal class RuffTest : CommandFactoryTest(RuffCommand::class.java) {
    
    private lateinit var ruff: Ruff
    
    override fun setUp() {
        super.setUp()
        
        ruff = project.ruff!!
    }
    
    @Test
    fun `test command classes`() {
        commandClassTest<CheckCommand>(listOf("check"))
        commandClassTest<FixCommand>(listOf("check"))
        commandClassTest<OptimizeImportsCommand>(listOf("check"))
        commandClassTest<OrganizeImportsCommand>(listOf("check"))
        commandClassTest<ShowSettingsCommand>(listOf("check"))
        commandClassTest<CleanCommand>(listOf("clean"))
        commandClassTest<ConfigCommand>(listOf("config"))
        commandClassTest<LinterCommand>(listOf("linter"))
        commandClassTest<FormatCommand>(listOf("format"))
        commandClassTest<RuleCommand>(listOf("rule"))
        commandClassTest<VersionCommand>(listOf("version"))
    }

    @Test
    fun `test check 1 - path, not all fixable`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.check(text, path, considerAllFixable = false)
        
        commandTest<CheckCommand>(command, emptyList())
    }
    
    @Test
    fun `test check 2 - no path, not all fixable`() {
        val text = randomText()
        val command = ruff.check(text, path = null, considerAllFixable = false)
        
        commandTest<CheckCommand>(command, emptyList())
    }
    
    @Test
    fun `test check`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        val allFixable = boolean
        
        val command = ruff.check(text, path, allFixable)
        val arguments = command.arguments
        
        assertEquals(listOf("check"), command.subcommands)
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
        
        if (allFixable) {
            assertTrue(arguments include listOf("--fixable", "ALL"))
        }
    }
    
    @Test
    fun `test checkProject`() {
        val allFixable = boolean
        
        val command = ruff.checkProject(allFixable)
        val arguments = command.arguments
        
        assertEquals(listOf("check"), command.subcommands)
        assertEquals(projectPath, command.workingDirectory)
        
        assertContains(arguments, "--no-fix")
        assertContains(arguments, "--exit-zero")
        assertContains(arguments, "--quiet")
        
        assertTrue(arguments include listOf("--output-format", "json"))
        
        if (allFixable) {
            assertTrue(arguments include listOf("--fixable", "ALL"))
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
        
        assertEquals(listOf("format"), command.subcommands)
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
        
        assertEquals(listOf("clean"), command.subcommands)
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
        
        assertEquals(listOf("rule"), command.subcommands)
        assertEquals(listOf(code), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test allRules`() {
        val command = ruff.allRulesInfo()
        val arguments = command.arguments
        
        assertEquals(listOf("rule"), command.subcommands)
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
        
        assertEquals(listOf("config"), command.subcommands)
        assertEquals(listOf(option, "--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test allConfig`() {
        val command = ruff.allOptionsInfo()
        
        assertEquals(listOf("config"), command.subcommands)
        assertEquals(listOf("--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test linter`() {
        val command = ruff.linter()
        
        assertEquals(listOf("linter"), command.subcommands)
        assertEquals(listOf("--output-format", "json"), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test version`() {
        val command = ruff.version()
        
        assertEquals(listOf("version"), command.subcommands)
        assertEquals(emptyList<String>(), command.arguments)
        assertEquals(projectPath, command.workingDirectory)
    }
    
    @Test
    fun `test optimizeImports`() {
        val text = randomText()
        val path = randomPath().orRandomlyNull()
        
        val command = ruff.optimizeImports(text, path)
        val arguments = command.arguments
        
        assertEquals(listOf("check"), command.subcommands)
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
        
        assertEquals(listOf("check"), command.subcommands)
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
        
        val command = ruff.fixAll(text, path, unsafe = unsafeFixes)
        val arguments = command.arguments
        
        assertEquals(listOf("check"), command.subcommands)
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
        
        assertEquals(listOf("check"), command.subcommands)
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
