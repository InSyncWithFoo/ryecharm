package insyncwithfoo.ryecharm

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.guessProjectDir
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import kotlin.io.path.listDirectoryEntries


internal abstract class LanguageServerTestCase : PlatformTestCase() {
    
    override fun setUp() {
        super.setUp()
        fixture.testDataPath = this.testDataPath
    }
    
    final override fun createMyFixture(): CodeInsightTestFixture? {
        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        
        val name = """${this::class.java.name}.$name"""
        val projectBuilder = fixtureFactory.createFixtureBuilder(name)
        
        return fixtureFactory.createCodeInsightFixture(projectBuilder.fixture)
    }
    
    protected fun diagnosticTest(filePath: String) {
        thisLogger().warn(project.basePath!!)
        thisLogger().warn(project.basePath!!.toPathOrNull()!!.toFile().exists().toString())
        thisLogger().warn(projectPath?.listDirectoryEntries()?.toList()?.toString())
        
        fileBasedTest(filePath) {
            thisLogger().warn(file.virtualFile.toNioPath().toString())
            thisLogger().warn(file.virtualFile.toNioPath().toFile().exists().toString())
            
            fixture.checkLspHighlighting()
        }
    }
    
}
