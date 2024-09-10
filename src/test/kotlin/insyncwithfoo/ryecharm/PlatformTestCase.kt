package insyncwithfoo.ryecharm

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase


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
    
}
