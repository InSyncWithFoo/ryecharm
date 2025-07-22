package insyncwithfoo.ryecharm.ruff.lsp

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.ModuleFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.util.io.createParentDirectories
import insyncwithfoo.ryecharm.configurations.add
import insyncwithfoo.ryecharm.configurations.changeRuffConfigurations
import insyncwithfoo.ryecharm.configurations.changeRuffOverrides
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruffExecutable
import insyncwithfoo.ryecharm.lspServerManager
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.div


class DartHighlightingTest : CodeInsightFixtureTestCase<ModuleFixtureBuilder<ModuleFixture>>() {
    
    protected val projectPath: Path
        get() = Path.of(project.basePath!!)
    
    override fun setUp() {
        super.setUp()
        (myFixture as CodeInsightTestFixtureImpl).canChangeDocumentDuringHighlighting(true)
        myFixture.testDataPath = "src/test/testData/ruff/lsp/LanguageServerTest"
        
        project.changeRuffConfigurations {
            runningMode = RunningMode.LSP
            project.changeRuffOverrides { add(::runningMode.name) }
        }
    }
    
    fun testUnresolvedReference() {
        myFixture.configureByFile("F401.py")
        
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
        myFixture.checkLspHighlighting()
    }
}
