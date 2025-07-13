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
    
    private fun randomRuleCode() = buildString {
        val linterPrefix = buildString(1..5) { lowercase }
        val ruleNumber = buildString(3..6) { digit }
        
        append(linterPrefix)
        append(ruleNumber)
    }

    private fun randomOptionName() = buildString(10..30) {
        listOf(lowercase, '.', '-').random()
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
        
        commandTest<CheckCommand>(command, expectedArguments, stdin = text)
    }
    
    @Test
    fun `test check 2 - path, all fixable`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.check(text, path, considerAllFixable = true)
        
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
            "-", "--quiet",
            "--stdin-filename", path.toString(),
            "--range", "1:2-3:4"
        )

        commandTest<FormatCommand>(command, expectedArguments, stdin = text)
    }

    @Test
    fun `test clean`() {
        val path = randomPath()
        val command = ruff.clean(path)

        commandTest<CleanCommand>(command, workingDirectory = path)
    }
    
    @Test
    fun `test rule`() {
        val code = randomRuleCode()
        val command = ruff.ruleInfo(code)
        
        val expectedArguments = listOf(code)

        commandTest<RuleCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test allRules`() {
        val command = ruff.allRulesInfo()
        
        val expectedArguments = listOf(
            "--all",
            "--output-format", "json"
        )

        commandTest<RuleCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test config`() {
        val option = randomOptionName()
        val command = ruff.optionInfo(option)
        
        val expectedArguments = listOf(
            option,
            "--output-format", "json"
        )

        commandTest<ConfigCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test allConfig`() {
        val command = ruff.allOptionsInfo()

        val expectedArguments = listOf("--output-format", "json")

        commandTest<ConfigCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test linter`() {
        val command = ruff.allLintersInfo()
        
        val expectedArguments = listOf("--output-format", "json")

        commandTest<LinterCommand>(command, expectedArguments)
    }
    
    @Test
    fun `test version`() {
        val command = ruff.version()
        
        commandTest<VersionCommand>(command)
    }
    
    @Test
    fun `test optimizeImports 1 - no path`() {
        val text = randomText()
        val command = ruff.optimizeImports(text, path = null)
        
        val expectedArguments = listOf(
            "--fix", "--fix-only", "--exit-zero", "--quiet", "-",
            "--select", "I,F401"
        )

        commandTest<OptimizeImportsCommand>(command, expectedArguments, stdin = text)
    }
    
    @Test
    fun `test optimizeImports 2 - path`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.optimizeImports(text, path)
        
        val expectedArguments = listOf(
            "--fix", "--fix-only", "--exit-zero", "--quiet", "-",
            "--select", "I,F401",
            "--stdin-filename", path.toString()
        )

        commandTest<OptimizeImportsCommand>(command, expectedArguments, stdin = text)
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
    fun `test organizeImports 1 - no path`() {
        val text = randomText()
        val command = ruff.organizeImports(text, path = null)
        
        val expectedArguments = listOf(
            "--fix", "--fix-only", "--exit-zero", "--quiet", "-",
            "--select", "I"
        )

        commandTest<OrganizeImportsCommand>(command, expectedArguments, stdin = text)
    }

    @Test
    fun `test organizeImports 2 - path`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.organizeImports(text, path)
        
        val expectedArguments = listOf(
            "--fix", "--fix-only", "--exit-zero", "--quiet", "-",
            "--select", "I",
            "--stdin-filename", path.toString()
        )
        
        commandTest<OrganizeImportsCommand>(command, expectedArguments, stdin = text)
    }
    
}
