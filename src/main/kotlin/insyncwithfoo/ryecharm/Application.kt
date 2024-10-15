package insyncwithfoo.ryecharm

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager


internal val application: Application
    get() = ApplicationManager.getApplication()


internal val fileDocumentManager: FileDocumentManager
    get() = FileDocumentManager.getInstance()


internal val editorFactory: EditorFactory
    get() = EditorFactory.getInstance()


internal fun invokeLater(action: () -> Unit) {
    application.invokeLater(action)
}
