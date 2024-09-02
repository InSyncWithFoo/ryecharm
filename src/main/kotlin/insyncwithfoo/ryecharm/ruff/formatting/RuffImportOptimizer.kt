package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.lang.ImportOptimizer
import com.intellij.openapi.command.writeCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.psiDocumentManager
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.runFormattingOperation
import insyncwithfoo.ryecharm.runInBackground


internal class RuffImportOptimizer : ImportOptimizer {
    
    override fun supports(file: PsiFile) =
        file.project.ruffConfigurations.run { formatting && formatOnOptimizeImports && file.isSupportedByRuff }
    
    override fun processFile(file: PsiFile) =
        file.makeProcessor() ?: Runnable {}
    
    private fun PsiFile.makeProcessor(): Runnable? {
        val ruff = project.ruff ?: return null
        val document = viewProvider.document ?: return null
        val path = virtualFile?.toNioPathOrNull()
        
        val command = ruff.optimizeImports(document.text, path)
        
        return Runnable {
            project.optimizeImportsAndLoadResult(command, document)
        }
    }
    
    private fun Project.optimizeImportsAndLoadResult(command: Command, document: Document) = runFormattingOperation {
        val output = runInBackground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            if (!newText.contentEquals(document.charsSequence)) {
                writeNewTextBack(document, output.stdout)
            }
        }
    }
    
    @Suppress("UnstableApiUsage", "DialogTitleCapitalization")
    private fun Project.writeNewTextBack(document: Document, newText: String) = runFormattingOperation {
        writeCommandAction(this@writeNewTextBack, message("progresses.command.ruff.optimizeImports")) {
            document.replaceString(0, document.textLength, newText)
            psiDocumentManager.commitDocument(document)
        }
    }
    
}
