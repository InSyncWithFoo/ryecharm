package insyncwithfoo.ryecharm

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import kotlin.reflect.KClass


private val KClass<*>.qualifiedNameWithoutPackagePrefix: String
    get() = qualifiedName!!.removePrefix("insyncwithfoo.ryecharm.")


private val KClass<*>.testDataPath: String
    get() = "src/test/testData/${qualifiedNameWithoutPackagePrefix.replace(".", "/")}"


internal abstract class PlatformTestCase : LightPlatformCodeInsightFixture4TestCase() {
    
    protected val fixture: CodeInsightTestFixture
        get() = myFixture
    
    protected val editor: Editor
        get() = fixture.editor
    
    protected val file: PsiFile
        get() = fixture.file
    
    protected fun fileBasedTest(filePath: String, test: () -> Unit) {
        fixture.configureByFile(filePath)
        test()
    }
    
    final override fun getTestDataPath() = this::class.testDataPath
    
}
