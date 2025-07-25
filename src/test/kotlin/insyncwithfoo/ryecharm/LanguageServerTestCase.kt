package insyncwithfoo.ryecharm

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import java.nio.file.Path


internal abstract class LanguageServerTestCase : HeavyPlatformTestCase() {
    
    protected lateinit var fixture: CodeInsightTestFixture
    
    protected val projectPath: Path
        get() = Path.of(project.basePath!!)
    
    override fun setUp() {
        super.setUp()
        
        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        val fixtureBuilder = fixtureFactory.createFixtureBuilder("", projectPath, true)
        
        fixture = CodeInsightTestFixtureImpl(
            fixtureBuilder.fixture,
            TempDirTestFixtureImpl()
        )
        fixture.testDataPath = this::class.testDataPath
    }
    
}
