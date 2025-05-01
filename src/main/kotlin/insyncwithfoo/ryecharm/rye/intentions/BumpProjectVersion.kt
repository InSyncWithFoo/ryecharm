package insyncwithfoo.ryecharm.rye.intentions

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
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.VersionBumpType
import insyncwithfoo.ryecharm.rye.commands.rye
import insyncwithfoo.ryecharm.unknownError


internal abstract class BumpProjectVersion(val bumpType: VersionBumpType) :
    ExternalIntentionAction, WriteIntentionAction, DumbAware
{
    
    override fun getFamilyName() =
        "${message("intentions.rye.bumpProjectVersion.familyName")}: $bumpType"
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) =
        editor != null && file?.virtualFile?.isPyprojectToml == true
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val document = file!!.viewProvider.document ?: return
        val rye = project.rye
        
        if (rye == null) {
            project.couldNotConstructCommandFactory<Rye>(
                """
                |Was trying to bump project version.
                """.trimMargin()
            )
            return
        }
        
        val command = rye.version(bumpType)
        
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
        val newVersion = extractNewVersion(output.stdout)
            ?: return unknownError(command, output)
        
        val successfulContent = message("notifications.versionSetTo.title", newVersion)
        processCompletedSuccessfully(successfulContent)
    }
    
    private fun extractNewVersion(stdout: String): String? {
        val version = """(?<=version (?:set|bumped) to )\S+""".toRegex()
        val nonWhitespaceOnly = """^\S+$""".toRegex()
        
        val trimmed = stdout.trim()
        
        return when {
            trimmed.matches(nonWhitespaceOnly) -> trimmed
            else -> version.find(stdout)?.value
        }
    }
    
}


internal class BumpProjectMajorVersion : BumpProjectVersion(VersionBumpType.MAJOR)

internal class BumpProjectMinorVersion : BumpProjectVersion(VersionBumpType.MINOR)

internal class BumpProjectPatchVersion : BumpProjectVersion(VersionBumpType.PATCH)
