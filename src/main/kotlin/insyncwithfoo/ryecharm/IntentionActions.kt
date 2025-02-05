package insyncwithfoo.ryecharm

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PriorityAction.Priority
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile


internal fun FileDocumentManager.saveAllDocumentsAsIs() {
    unsavedDocuments.forEach { saveDocumentAsIs(it) }
}


internal interface WriteIntentionAction : IntentionAction {
    override fun startInWriteAction() = true
}


internal interface ExternalIntentionAction : IntentionAction {
    
    /**
     * Always return an empty preview, since external processes
     * should only be handled on an ad-hoc basis.
     * 
     * This overrides the default implementation,
     * which only ever unnecessarily calls [invoke].
     * 
     * @see IntentionAction.generatePreview
     */
    override fun generatePreview(project: Project, editor: Editor, file: PsiFile): IntentionPreviewInfo {
        return IntentionPreviewInfo.EMPTY
    }
    
}


internal interface TopPriorityAction : PriorityAction {
    override fun getPriority() = Priority.TOP
}
