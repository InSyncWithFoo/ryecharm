package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.CommandFactoryTest
import org.junit.Test


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
    
    private fun randomVersion() = buildString(5..10) {
        listOf(lowercase, '.', '-').random()
    }
    
    private fun randomPackageList() = buildList((0..10).random()) {
        add(randomPEP508Name())
    }
    
    @Test
    fun `test command classes`() {
        assertEquals(13, subclassCount<UVCommand>())
        
        commandClassTest<AddCommand>(listOf("add"))
        commandClassTest<InitCommand>(listOf("init"))
        commandClassTest<InstallDependenciesCommand>(listOf("sync"))
        commandClassTest<RemoveCommand>(listOf("remove"))
        commandClassTest<SyncCommand>(listOf("sync"))
        commandClassTest<UpgradeCommand>(listOf("add"))
        commandClassTest<VenvCommand>(listOf("venv"))
        commandClassTest<PipCompileCommand>(listOf("pip", "compile"))
        commandClassTest<PipListCommand>(listOf("pip", "list"))
        commandClassTest<PipTreeCommand>(listOf("pip", "tree"))
        commandClassTest<VersionCommand>(listOf("version"))
        commandClassTest<SelfUpdateCommand>(listOf("self", "update"))
        commandClassTest<SelfVersionCommand>(listOf("self", "version"))
    }
    
    @Test
    fun `test pip tree 1 - normal, deduped, no specifiers, no latest versions, no interpreter`() {
        val `package` = randomPEP508Name()
        val depth = (0..1000).random()
        val command = uv.pipTree(
            `package`,
            inverted = false,
            showVersionSpecifiers = false,
            showLatestVersions = false,
            dedupe = true,
            depth = depth,
            interpreter = null
        )
        
        commandTest<PipTreeCommand>(command) {
            assertArgumentsContain("--package" to `package`)
            assertArgumentsContain("--depth" to depth.toString())
        }
    }
    
    @Test
    fun `test pip tree 2 - inverted, not deduped, with specifiers, with latest versions, with interpreter`() {
        val (`package`, interpreter) = Pair(randomPEP508Name(), randomPath())
        val depth = (0..1000).random()
        val command = uv.pipTree(
            `package`,
            inverted = true,
            showVersionSpecifiers = true,
            showLatestVersions = true,
            dedupe = false,
            depth = depth,
            interpreter = interpreter
        )
        
        commandTest<PipTreeCommand>(command) {
            assertArgumentsContain("--invert", "--show-version-specifiers", "--outdated", "--no-dedupe")
            assertArgumentsContain("--package" to `package`)
            assertArgumentsContain("--depth" to depth.toString())
            assertArgumentsContain("--python" to interpreter.toString())
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
    fun `test pip compile 1 - with header`() {
        val packages = randomPackageList()
        val text = packages.joinToString("\n")
        val command = uv.pipCompile(packages, noHeader = false)
        
        commandTest<PipCompileCommand>(command, stdin = text) {
            assertArgumentsContain("-")
        }
    }
    
    @Test
    fun `test pip compile 2 - no header`() {
        val packages = randomPackageList()
        val text = packages.joinToString("\n")
        val command = uv.pipCompile(packages, noHeader = true)
        
        commandTest<PipCompileCommand>(command, stdin = text) {
            assertArgumentsContain("--no-header", "-")
        }
    }
    
    @Test
    fun `test pip list 1 - no interpreter`() {
        val command = uv.pipList(interpreter = null)
        
        commandTest<PipListCommand>(command) {
            assertArgumentsContain("--quiet")
            assertArgumentsContain("--format" to "json")
        }
    }
    
    @Test
    fun `test pip list 2 - with interpreter`() {
        val interpreter = randomPath()
        val command = uv.pipList(interpreter)
        
        commandTest<PipListCommand>(command) {
            assertArgumentsContain("--quiet")
            assertArgumentsContain("--format" to "json")
            assertArgumentsContain("--python" to interpreter.toString())
        }
    }
    
    @Test
    fun `test install group`() {
        val name = randomPEP508Name()
        val command = uv.installGroup(name)
        
        commandTest<InstallDependenciesCommand>(command) {
            assertArgumentsContain("--group" to name)
        }
    }
    
    @Test
    fun `test install all groups`() {
        val command = uv.installAllGroups()
        
        commandTest<InstallDependenciesCommand>(command) {
            assertArgumentsContain("--all-groups")
        }
    }
    
    @Test
    fun `test install extra`() {
        val name = randomPEP508Name()
        val command = uv.installExtra(name)
        
        commandTest<InstallDependenciesCommand>(command) {
            assertArgumentsContain("--extra" to name)
        }
    }
    
    @Test
    fun `test install all extras`() {
        val command = uv.installAllExtras()
        
        commandTest<InstallDependenciesCommand>(command) {
            assertArgumentsContain("--all-extras")
        }
    }
    
    @Test
    fun `test version - get`() {
        val command = uv.getProjectVersion()
        
        commandTest<VersionCommand>(command) {
            assertArgumentsContain("--short")
        }
    }
    
    @Test
    fun `test version - bump major`() {
        val bumpType = VersionBumpType.MAJOR
        val command = uv.bumpProjectVersion(bumpType)
        
        commandTest<VersionCommand>(command) {
            assertArgumentsContain("--short")
            assertArgumentsContain("--bump" to "major")
        }
    }
    
    @Test
    fun `test version - set`() {
        val newVersion = randomVersion()
        val command = uv.setProjectVersion(newVersion)
        
        commandTest<VersionCommand>(command) {
            assertArgumentsContain(newVersion, "--short")
        }
    }
    
    @Test
    fun `test venv 1 - no name`() {
        val interpreter = randomPath()
        val command = uv.venv(interpreter, name = null)
        
        commandTest<VenvCommand>(command) {
            assertArgumentsContain("--python" to interpreter.toString())
        }
    }
    
    @Test
    fun `test venv 2 - with name`() {
        val (interpreter, name) = Pair(randomPath(), randomPEP508Name())
        val command = uv.venv(interpreter, name = name)
        
        commandTest<VenvCommand>(command) {
            assertArgumentsContain(name)
            assertArgumentsContain("--python" to interpreter.toString())
        }
    }
    
}
