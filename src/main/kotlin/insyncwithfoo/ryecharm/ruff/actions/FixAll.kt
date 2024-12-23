package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.jupyter.core.jupyter.helper.editor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noDocumentFound
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.paste
import insyncwithfoo.ryecharm.psiDocumentManager
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.intentions.IntentionCoroutine
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.runWriteCommandAction


internal class FixAll : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val editor = event.editor ?: return project.noDocumentFound()
        
        val ruff = project.ruff
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to fix all fixable problems.
                """.trimMargin()
            )
            return
        }
        
        val document = editor.document
        val file = project.psiDocumentManager.getPsiFile(document)
            ?: return project.noDocumentFound()
        val path = file.virtualFile.toNioPath()
        
        val command = ruff.fixAll(document.text, path)
        
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
        runWriteCommandAction(message("progresses.command.ruff.fixAll")) {
            file.paste(newText)
        }
    }
    
}
