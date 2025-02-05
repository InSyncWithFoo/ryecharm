package insyncwithfoo.ryecharm.ruff.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.ExternalIntentionAction
import insyncwithfoo.ryecharm.configurations.ruff.RunningMode
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.paste
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.runWriteCommandAction


internal class OrganizeImports : ExternalIntentionAction, LowPriorityAction, DumbAware {
    
    override fun startInWriteAction() = false
    
    override fun getFamilyName() = message("intentions.ruff.organizeImports.familyName")
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val configurations = project.ruffConfigurations
        
        return when {
            configurations.runningMode != RunningMode.COMMAND_LINE -> false
            !configurations.quickFixes || !configurations.organizeImports -> false
            else -> editor != null && file?.isSupportedByRuff == true
        }
    }
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val document = file!!.viewProvider.document ?: return
        val path = file.virtualFile.toNioPath()
        val ruff = project.ruff
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to organize imports:
                |$path
                """.trimMargin()
            )
            return
        }
        
        val command = ruff.organizeImports(document.text, path)
        
        project.runCommandAndLoadResult(command, file)
    }
    
    private fun Project.runCommandAndLoadResult(command: Command, file: PsiFile) = launch<IntentionCoroutine> {
        val output = runInForeground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            writeNewTextBack(file, newText)
        }
    }
    
    private fun Project.writeNewTextBack(file: PsiFile, newText: String) = launch<IntentionCoroutine> {
        runWriteCommandAction(message("progresses.command.ruff.organizeImports")) {
            file.paste(newText)
        }
    }
    
}
