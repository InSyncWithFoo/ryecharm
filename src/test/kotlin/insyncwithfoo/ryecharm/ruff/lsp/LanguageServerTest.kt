package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.psi.PsiFile
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import com.intellij.testFramework.fixtures.TempDirTestFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import com.intellij.util.io.createParentDirectories
import insyncwithfoo.ryecharm.canBeLintedByRuff
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.localFileSystem
import insyncwithfoo.ryecharm.lspServerManager
import insyncwithfoo.ryecharm.testDataPath
import insyncwithfoo.ryecharm.toPathOrNull
import org.junit.Test
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.listDirectoryEntries


internal class LanguageServerTest : LightPlatformCodeInsightFixture4TestCase() {
    
    protected val fixture: CodeInsightTestFixture
        get() = myFixture
    
    protected val editor: Editor
        get() = fixture.editor
    
    protected val file: PsiFile
        get() = fixture.file
    
    protected val projectPath: Path
        get() = Path.of(project.basePath!!)
    
    override fun setUp() {
        super.setUp()
        
        (fixture as? CodeInsightTestFixtureImpl)?.canChangeDocumentDuringHighlighting(true)
        
        thisLogger().warn("f: ${fixture.testDataPath}")
        thisLogger().warn("f: ${this::class.testDataPath}")
        
        fixture.testDataPath = this::class.testDataPath
        
        // if (!projectPath!!.toFile().exists()) {
        //     projectPath!!.createDirectory()
        // }
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    override fun getTestDataPath() =
        Path.of(this::class.testDataPath).toAbsolutePath().toString()
    
    @Test
    fun `test diagnostics - python file`() {
        thisLogger().warn(Path.of("").toAbsolutePath().toString())
        thisLogger().warn((Path.of("").toAbsolutePath() / this::class.testDataPath).toString())
        
        thisLogger().warn(project.basePath!!)
        thisLogger().warn(project.basePath!!.toPathOrNull()!!.toFile().exists().toString())
        
        try {
            thisLogger().warn(Path.of(project.basePath!!).listDirectoryEntries().toList().toString())
        } catch (_: Throwable) {
            thisLogger().warn("LDE failed")
        }
        
        thisLogger().warn(fixture.testDataPath)
        thisLogger().warn(this::class.testDataPath)
        
        fixture.configureByFile("F401.py")
        
        projectPath.createParentDirectories()
        try {
            projectPath.createDirectory()
        } catch (_: IOException) {
            thisLogger().warn("Exists: $projectPath")
        }
        (projectPath / file.virtualFile.path.removePrefix("/")).createParentDirectories()
        try {
            (projectPath / file.virtualFile.path.removePrefix("/")).createFile()
        } catch (_: IOException) {
            thisLogger().warn("Exists: ${projectPath / file.virtualFile.path.removePrefix("/")}")
        }
        
        thisLogger().warn(file.virtualFile.path)
        thisLogger().warn(file.virtualFile.presentableUrl)
        thisLogger().warn(file.virtualFile.toString())
        // thisLogger().warn(file.virtualFile.toNioPath().toString())
        // thisLogger().warn(file.virtualFile.toNioPath().toFile().exists().toString())
        thisLogger().warn(file.virtualFile.canBeLintedByRuff(project).toString())
        thisLogger().warn(Pair(project.ruffExecutable, project.ruffConfigurations).toString())
        
        thisLogger().warn(project.lspServerManager.getServersForProvider(RuffServerSupportProvider::class.java)
                              .toList().toString())
        
        fixture.checkLspHighlighting()
    }
    
    // @Test
    // fun `test diagnostics - pyproject toml`() = diagnosticTest("pyproject.toml")
    
}
