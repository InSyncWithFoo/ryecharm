package insyncwithfoo.ryecharm

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.BaseProjectDirectories.Companion.getBaseDirectories
import com.intellij.platform.lsp.tests.checkLspHighlighting
import com.intellij.platform.lsp.tests.checkLspHighlightingForData
import com.intellij.psi.PsiFile
import com.intellij.testFramework.ExpectedHighlightingData
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import java.nio.file.Path
import kotlin.reflect.KClass


private val KClass<*>.qualifiedNameWithoutPackagePrefix: String
    get() = qualifiedName!!.removePrefix("${RyeCharm.ID}.")


private val KClass<*>.testDataPath: String
    get() = "src/test/testData/${qualifiedNameWithoutPackagePrefix.replace(".", "/")}"


internal abstract class PlatformTestCase : LightPlatformCodeInsightFixture4TestCase() {
    
    protected val fixture: CodeInsightTestFixture
        get() = myFixture
    
    protected val editor: Editor
        get() = fixture.editor
    
    protected val file: PsiFile
        get() = fixture.file
    
    protected val projectPath: Path?
        get() = project.basePath?.toPathOrNull()
    
    override fun setUp() {
        super.setUp()
        
        (fixture as? CodeInsightTestFixtureImpl)?.canChangeDocumentDuringHighlighting(true)
    }
    
    protected inline fun fileBasedTest(filePath: String, test: () -> Unit) {
        fixture.configureByFile(filePath)
        test()
    }
    
    protected fun languageServerDiagnosticTest(filePath: String) =
        fileBasedTest(filePath) {
            thisLogger().warn(project.basePath)
            thisLogger().warn(project.baseDir.toString())
            thisLogger().warn(project.baseDir.path)
            thisLogger().warn(project.getBaseDirectories().toList().toString())
            
            fixture.checkLspHighlighting()
        }
    
    final override fun getTestDataPath() = this::class.testDataPath
    
}
