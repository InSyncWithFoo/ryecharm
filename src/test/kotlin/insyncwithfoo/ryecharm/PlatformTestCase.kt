package insyncwithfoo.ryecharm

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase


internal abstract class PlatformTestCase : BasePlatformTestCase() {
    
    protected val fixture by ::myFixture
    
    protected val editor: Editor
        get() = fixture.editor
    
    protected val file: PsiFile
        get() = fixture.file
    
}
