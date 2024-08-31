package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.openapi.command.writeCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.psiDocumentManager
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.runFormattingOperation
import insyncwithfoo.ryecharm.runInBackground


// TODO: Use com.intellij.openapi.roots.ProjectFileIndex.isInProject
private operator fun Project.contains(file: VirtualFile?) =
    basePath?.let { file?.canonicalPath?.startsWith(it) } ?: false


internal class RuffOnSaveFormatter(private val project: Project) : FileDocumentManagerListener {
    
    override fun beforeDocumentSaving(document: Document) {
        val file = project.psiDocumentManager.getPsiFile(document) ?: return
        val virtualFile = file.virtualFile
        val configurations = project.ruffConfigurations
        
        when {
            !configurations.formatOnSave -> return
            configurations.formatOnSaveProjectFilesOnly && virtualFile !in project -> return
            !file.isSupportedByRuff -> return
        }
        
        val ruff = project.ruff ?: return
        val command = ruff.format(document.text, virtualFile?.toNioPathOrNull())
        
        project.runRuffFormatAndLoadResult(command, document)
    }
    
    private fun Project.runRuffFormatAndLoadResult(command: Command, document: Document) = runFormattingOperation {
        val output = runInBackground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            if (!newText.contentEquals(document.charsSequence)) {
                writeNewTextBack(document, output.stdout)
            }
        }
    }
    
    @Suppress("UnstableApiUsage")
    private fun Project.writeNewTextBack(document: Document, newText: String) = runFormattingOperation {
        writeCommandAction(this@writeNewTextBack, message("progresses.command.ruff.format")) {
            document.replaceString(0, document.textLength, newText)
            psiDocumentManager.commitDocument(document)
        }
    }
    
}
