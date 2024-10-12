package insyncwithfoo.ryecharm.common.injections

import com.jetbrains.python.requirements.RequirementsFile
import org.junit.Test


internal class RequirementsInjectorTest : LanguageInjectionTestCase() {
    
    @Test
    fun `test project optional-dependencies`() = fileBasedTest("projectOptionalDependencies/pyproject.toml") {
        assertEquals(2, fragments.size)
    }
    
    @Test
    fun `test dependency-groups`() = fileBasedTest("dependencyGroups/pyproject.toml") {
        assertEquals(1, fragments.size)
    }
    
    @Test
    fun `test other keys`() {
        val directoriesAndKeyNames = mapOf(
            "constraintDependencies" to "constraint-dependencies",
            "devDependencies" to "dev-dependencies",
            "overrideDependencies" to "override-dependencies",
            "upgradePackage" to "upgrade-package",
            "pipUpgradePackage" to "upgrade-package",
            "pipUpgradePackageInline" to "pip.upgrade-package"
        )
        val fileNames = listOf("pyproject.toml", "uv.toml")
        
        directoriesAndKeyNames.forEach { (directory, key) ->
            val arrayKeyAtLineStart = """(?m)^${Regex.escape(key)} = \[\n""".toRegex()
            
            fileNames.forEach { fileName ->
                fileBasedTest("$directory/$fileName") {
                    val fragment = fragments.single()
                    
                    assertTrue(arrayKeyAtLineStart.containsMatchIn(file.text))
                    assertInstanceOf(fragment, RequirementsFile::class.java)
                }
            }
        }
    }
    
}
