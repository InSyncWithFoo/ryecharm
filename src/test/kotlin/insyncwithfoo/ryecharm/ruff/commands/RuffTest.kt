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
    fun `test check 1 - no path, not all fixable`() {
        val text = randomText()
        val command = ruff.check(text, path = null, considerAllFixable = false)
        
        val expectedArguments = listOf(
            "--no-fix", "--exit-zero", "--quiet", "-",
            "--output-format", "json"
        )
        
        commandTest<CheckCommand>(command, stdin = text)
    }
    
    @Test
    fun `test check 2 - path, all fixable`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.check(text, path, considerAllFixable = false)
        
        val expectedArguments = listOf(
            "--no-fix", "--exit-zero", "--quiet", "-",
            "--output-format", "json",
            "--stdin-filename", path.toString(),
            "--fixable", "ALL"
        )
        
        commandTest<CheckCommand>(command, expectedArguments, stdin = text)
    }
    
    @Test
    fun `test checkProject 1 - not all fixable`() {
        val command = ruff.checkProject(considerAllFixable = false)

        val expectedArguments = listOf(
            "--no-fix", "--exit-zero", "--quiet",
            "--output-format", "json"
        )

        commandTest<CheckCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test checkProject 2 - all fixable`() {
        val command = ruff.checkProject(considerAllFixable = true)

        val expectedArguments = listOf(
            "--no-fix", "--exit-zero", "--quiet",
            "--output-format", "json",
            "--fixable", "ALL"
        )

        commandTest<CheckCommand>(command, expectedArguments)
    }

    @Test
    fun `test format 1 - no path, no range, not quiet`() {
        val text = randomText()
        val command = ruff.format(text, path = null, range = null, quiet = false)

        val expectedArguments = listOf("-")

        commandTest<FormatCommand>(command, expectedArguments, stdin = text)
    }

    @Test
    fun `test format 2 - path, range, quiet`() {
        val (text, path) = Pair(randomText(), randomPath())
        val range = OneBasedRange(1 to 2, 3 to 4)
        val command = ruff.format(text, path, range, quiet = true)

        val expectedArguments = listOf(
            "-",
            "--stdin-filename", path.toString(),
            "--range", "1:2-3:4",
            "--quiet"
        )

        commandTest<FormatCommand>(command, expectedArguments, stdin = text)
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
        val command = ruff.ruleInfo(code)
        
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
        val command = ruff.optionInfo(option)
        
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
        val command = ruff.allLintersInfo()
        
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
