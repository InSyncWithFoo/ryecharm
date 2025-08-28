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


internal fun DataContext.getRelevantFile(): PsiFile? {
    file?.let { return it }
    
    val project = this.project ?: return null
    val editor = this.editor ?: return null
    
    return project.psiDocumentManager.getPsiFile(editor.document)
}


/**
 * @see com.jetbrains.python.hierarchy.call.PyCallHierarchyProvider.getTarget
 */
internal fun DataContext.getRelevantElement(): PsiElement? {
    element?.let { return it }
    
    val editor = this.editor ?: return null
    val file = getRelevantFile() ?: return null
    val offset = editor.caretModel.offset
    
    return file.findElementAt(offset)
}
