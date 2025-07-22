package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import com.intellij.testFramework.fixtures.TempDirTestFixture
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import insyncwithfoo.ryecharm.LanguageServerTestCase
import insyncwithfoo.ryecharm.PlatformTestCase
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.localFileSystem
import insyncwithfoo.ryecharm.testDataPath
import insyncwithfoo.ryecharm.toPathOrNull
import org.junit.Test
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectory
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.listDirectoryEntries


internal class LanguageServerTest : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        
        projectPath!!.createDirectory()
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    override fun createTempDirTestFixture() = TempDirTestFixtureImpl()
    
    private fun copyFileToProject(filePath: String): VirtualFile {
        val origin = Path.of(testDataPath, filePath)
        val target = projectPath!! / filePath
        
        target.createParentDirectories()
        origin.copyTo(target, overwrite = true)
        
        return localFileSystem.findFileByIoFile(target.toFile())!!
    }
    
    @Test
    fun `test diagnostics - python file`() {
        thisLogger().warn(Path.of("").toAbsolutePath().toString())
        thisLogger().warn((Path.of("").toAbsolutePath() / this::class.testDataPath).toString())
        
        thisLogger().warn(this.project.basePath!!)
        thisLogger().warn(this.project.basePath!!.toPathOrNull()!!.toFile().exists().toString())
        
        try {
            thisLogger().warn(Path.of(project.basePath!!).listDirectoryEntries().toList().toString())
        } catch (_: Throwable) {
            thisLogger().warn("LDE failed")
        }
        
        thisLogger().warn(myFixture.testDataPath)
        thisLogger().warn(this::class.testDataPath)
        
        // myFixture.configureByFile("F401.py")
        val testFile = copyFileToProject("F401.py")
        myFixture.openFileInEditor(testFile)
        
        thisLogger().warn(myFixture.file.virtualFile.toNioPath().toString())
        thisLogger().warn(myFixture.file.virtualFile.toNioPath().toFile().exists().toString())
        
        myFixture.checkLspHighlighting()
    }
    
    // @Test
    // fun `test diagnostics - pyproject toml`() = diagnosticTest("pyproject.toml")
    
}
