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


/**
 * Marker for intentions that should start in write action.
 */
internal interface WriteIntentionAction : IntentionAction {
    override fun startInWriteAction() = true
}


/**
 * Marker for intentions that are mainly based on external processes.
 * 
 * @see generatePreview
 */
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


/**
 * Marker for intentions that should be placed
 * at the very top of the context actions panel.
 */
internal interface TopPriorityAction : PriorityAction {
    override fun getPriority() = Priority.TOP
}
