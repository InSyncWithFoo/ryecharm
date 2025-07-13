package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.ExternalIntentionAction
import insyncwithfoo.ryecharm.WriteIntentionAction
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.fileDocumentManager
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.unknownError
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.VersionBumpType
import insyncwithfoo.ryecharm.uv.commands.uv
import insyncwithfoo.ryecharm.uv.commands.version
import org.jetbrains.annotations.VisibleForTesting


internal abstract class BumpProjectVersion(@VisibleForTesting val bumpType: VersionBumpType) :
    ExternalIntentionAction, WriteIntentionAction, DumbAware
{
    
    override fun getFamilyName() =
        "${message("intentions.uv.bumpProjectVersion.familyName")}: $bumpType"
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) =
        editor != null && file?.virtualFile?.isPyprojectToml == true
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val document = file!!.viewProvider.document ?: return
        val uv = project.uv
        
        if (uv == null) {
            project.couldNotConstructCommandFactory<UV>(
                """
                |Was trying to bump project version.
                """.trimMargin()
            )
            return
        }
        
        val command = uv.version(bumpType)
        
        if (project.path == null) {
            return noProjectFound()
        }
        
        fileDocumentManager.saveDocumentAsIs(document)
        project.runCommandAndLoadOutput(command, file)
    }
    
    private fun Project.runCommandAndLoadOutput(command: Command, file: PsiFile) = launch<IntentionCoroutine> {
        val output = runInForeground(command)
        val (asynchronous, recursive) = Pair(false, false)
        
        file.virtualFile?.refresh(asynchronous, recursive)
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            notifyNewVersion(command, output)
        }
    }
    
    private fun Project.notifyNewVersion(command: Command, output: ProcessOutput) {
        val newVersion = output.stdout.takeIf { it.isNotBlank() }
            ?: return unknownError(command, output)
        
        val successfulContent = message("notifications.versionSetTo.title", newVersion)
        processCompletedSuccessfully(successfulContent)
    }
    
}


internal class BumpProjectMajorVersion : BumpProjectVersion(VersionBumpType.MAJOR)

internal class BumpProjectMinorVersion : BumpProjectVersion(VersionBumpType.MINOR)

internal class BumpProjectPatchVersion : BumpProjectVersion(VersionBumpType.PATCH)
