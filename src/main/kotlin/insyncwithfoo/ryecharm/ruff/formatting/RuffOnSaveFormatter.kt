package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.paste
import insyncwithfoo.ryecharm.psiDocumentManager
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runWriteCommandAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Service(Service.Level.PROJECT)
private class RuffOnSaveFormatterCoroutine(val scope: CoroutineScope)


private fun Project.runTask(action: suspend CoroutineScope.() -> Unit) {
    service<RuffOnSaveFormatterCoroutine>().scope.launch(block = action)
}


// TODO: Use com.intellij.openapi.roots.ProjectFileIndex.isInProject
private operator fun Project.contains(file: VirtualFile?) =
    basePath?.let { file?.canonicalPath?.startsWith(it) } ?: false


internal class RuffOnSaveFormatter(private val project: Project) : FileDocumentManagerListener {
    
    override fun beforeDocumentSaving(document: Document) {
        val file = project.psiDocumentManager.getPsiFile(document) ?: return
        val virtualFile = file.virtualFile
        val path = virtualFile?.toNioPathOrNull()
        val configurations = project.ruffConfigurations
        
        when {
            !configurations.formatting -> return
            !configurations.formatOnSave -> return
            configurations.formatOnSaveProjectFilesOnly && virtualFile !in project -> return
            !file.isSupportedByRuff -> return
        }
        
        val ruff = project.ruff ?: return
        val command = ruff.format(document.text, path)
        
        project.runCommandAndLoadResult(command, file)
    }
    
    private fun Project.runCommandAndLoadResult(command: Command, file: PsiFile) = runTask {
        val output = runInBackground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            writeNewTextBack(file, newText)
        }
    }
    
    private fun Project.writeNewTextBack(file: PsiFile, newText: String) = runTask {
        runWriteCommandAction(message("progresses.command.ruff.format")) {
            file.paste(newText)
        }
    }
    
}
