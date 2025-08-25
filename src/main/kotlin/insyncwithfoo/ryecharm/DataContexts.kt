package insyncwithfoo.ryecharm

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile


internal val DataContext.project: Project?
    get() = getData(CommonDataKeys.PROJECT)


internal val DataContext.file: PsiFile?
    get() = getData(CommonDataKeys.PSI_FILE)


internal val DataContext.element: PsiElement?
    get() = getData(CommonDataKeys.PSI_ELEMENT)


internal val DataContext.editor: Editor?
    get() = getData(CommonDataKeys.EDITOR)


internal val DataContext.hostEditor: Editor?
    get() = getData(CommonDataKeys.HOST_EDITOR)
