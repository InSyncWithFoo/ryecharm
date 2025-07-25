package insyncwithfoo.ryecharm

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import com.intellij.testFramework.utils.io.createDirectory
import com.intellij.util.io.createParentDirectories
import java.nio.file.Path


internal abstract class LanguageServerTestCase : HeavyPlatformTestCase() {
    
    protected lateinit var fixture: CodeInsightTestFixture
    
    protected val projectPath: Path
        get() = Path.of(project.basePath!!)
    
    override fun setUp() {
        super.setUp()
        
        if (!projectPath.toFile().exists()) {
            projectPath.createParentDirectories()
            projectPath.createDirectory()
        }
        
        val fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory()
        val fixtureBuilder = fixtureFactory.createFixtureBuilder(this::class.qualifiedName!!, projectPath, true)
        
        fixture = CodeInsightTestFixtureImpl(
            fixtureBuilder.fixture,
            TempDirTestFixtureImpl()
        )
        fixture.testDataPath = this::class.testDataPath
        fixture.setUp()
    }
    
}
