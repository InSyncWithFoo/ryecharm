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
        
        commandTest<CheckCommand>(command, stdin = text) {
            assertArgumentsContain("--no-fix", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test check 2 - path, all fixable`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.check(text, path, considerAllFixable = true)
        
        commandTest<CheckCommand>(command, stdin = text) {
            assertArgumentsContain("--no-fix", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--output-format" to "json")
            assertArgumentsContain("--stdin-filename" to path.toString())
            assertArgumentsContain("--fixable" to "ALL")
        }
    }
    
    @Test
    fun `test checkProject 1 - not all fixable`() {
        val command = ruff.checkProject(considerAllFixable = false)
        
        commandTest<CheckCommand>(command) {
            assertArgumentsContain("--no-fix", "--exit-zero", "--quiet")
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test checkProject 2 - all fixable`() {
        val command = ruff.checkProject(considerAllFixable = true)
        
        commandTest<CheckCommand>(command) {
            assertArgumentsContain("--no-fix", "--exit-zero", "--quiet")
            assertArgumentsContain("--output-format" to "json")
            assertArgumentsContain("--fixable" to "ALL")
        }
    }
    
    @Test
    fun `test format 1 - no path, no range, not quiet`() {
        val text = randomText()
        val command = ruff.format(text, path = null, range = null, quiet = false)
        
        commandTest<FormatCommand>(command, stdin = text) {
            assertArgumentsContain("-")
        }
    }
    
    @Test
    fun `test format 2 - path, range, quiet`() {
        val (text, path) = Pair(randomText(), randomPath())
        val range = OneBasedRange(1 to 2, 3 to 4)
        val command = ruff.format(text, path, range, quiet = true)
        
        commandTest<FormatCommand>(command, stdin = text) {
            assertArgumentsContain("--quiet", "-")
            assertArgumentsContain("--stdin-filename" to path.toString())
            assertArgumentsContain("--range" to "1:2-3:4")
        }
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
        
        commandTest<RuleCommand>(command) {
            assertArgumentsContain(code)
        }
    }
    
    @Test
    fun `test allRules`() {
        val command = ruff.allRulesInfo()
        
        commandTest<RuleCommand>(command) {
            assertArgumentsContain("--all")
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test config`() {
        val option = randomOptionName()
        val command = ruff.optionInfo(option)
        
        commandTest<ConfigCommand>(command) {
            assertArgumentsContain(option)
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test allConfig`() {
        val command = ruff.allOptionsInfo()
        
        commandTest<ConfigCommand>(command) {
            assertArgumentsContain("--output-format" to "json")
        }
    }
    
    @Test
    fun `test linter`() {
        val command = ruff.allLintersInfo()
        
        commandTest<LinterCommand>(command) {
            assertArgumentsContain("--output-format" to "json")
        }
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
        
        commandTest<OptimizeImportsCommand>(command, stdin = text) {
            assertArgumentsContain("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--select" to "I,F401")
        }
    }
    
    @Test
    fun `test optimizeImports 2 - path`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.optimizeImports(text, path)
        
        commandTest<OptimizeImportsCommand>(command, stdin = text) {
            assertArgumentsContain("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--select" to "I,F401")
            assertArgumentsContain("--stdin-filename" to path.toString())
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
    fun `test organizeImports 1 - no path`() {
        val text = randomText()
        val command = ruff.organizeImports(text, path = null)
        
        commandTest<OrganizeImportsCommand>(command, stdin = text) {
            assertArgumentsContain("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--select" to "I")
        }
    }
    
    @Test
    fun `test organizeImports 2 - path`() {
        val (text, path) = Pair(randomText(), randomPath())
        val command = ruff.organizeImports(text, path)
        
        commandTest<OrganizeImportsCommand>(command, stdin = text) {
            assertArgumentsContain("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
            assertArgumentsContain("--select" to "I")
            assertArgumentsContain("--stdin-filename" to path.toString())
        }
    }
    
}
