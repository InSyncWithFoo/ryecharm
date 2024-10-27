package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.lang.ImportOptimizer
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.paste
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runWriteCommandAction
import kotlinx.coroutines.CoroutineScope


internal class RuffImportOptimizer : ImportOptimizer {
    
    override fun supports(file: PsiFile) =
        file.project.ruffConfigurations.run { formatting && formatOnOptimizeImports } && file.isSupportedByRuff
    
    override fun processFile(file: PsiFile) =
        file.makeProcessor() ?: Runnable {}
    
    private fun PsiFile.makeProcessor(): Runnable? {
        val ruff = project.ruff ?: return null
        val document = viewProvider.document ?: return null
        val path = virtualFile?.toNioPathOrNull()
        
        return Runnable {
            val command = ruff.optimizeImports(document.text, path)
            project.runCommandAndLoadResult(command, this)
        }
    }
    
    private fun Project.runCommandAndLoadResult(command: Command, file: PsiFile) = launch<Coroutine> {
        val output = runInBackground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            writeNewTextBack(file, newText)
        }
    }
    
    private fun Project.writeNewTextBack(file: PsiFile, newText: String) = launch<Coroutine> {
        runWriteCommandAction(message("progresses.command.ruff.optimizeImports")) {
            file.paste(newText)
        }
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
