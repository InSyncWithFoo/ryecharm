package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.ExternalIntentionAction
import insyncwithfoo.ryecharm.WriteIntentionAction
import insyncwithfoo.ryecharm.fileDocumentManager
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runIntention
import insyncwithfoo.ryecharm.saveAllDocumentsAsIs
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv


internal class SynchronizeProject : AnAction(), ExternalIntentionAction, WriteIntentionAction, DumbAware {
    
    override fun getFamilyName() = message("intentions.uv.sync.familyName")
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) =
        editor != null && file?.virtualFile?.isPyprojectToml == true
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val uv = project.uv ?: return project.unableToRunCommand()
        
        fileDocumentManager.saveAllDocumentsAsIs()
        project.runUVSyncAndReport(uv)
    }
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val uv = project.uv ?: return project.unableToRunCommand()
        
        project.runUVSyncAndReport(uv)
    }
    
    private fun Project.runUVSyncAndReport(uv: UV) = runIntention {
        val command = uv.sync()
        
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                processCompletedSuccessfully(message("notifications.environmentSynchronized.body"))
            }
        }
    }
    
}
