package insyncwithfoo.ryecharm.ruff.formatting

import com.intellij.lang.ImportOptimizer
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.isSupportedByRuff
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.paste
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runWriteCommandAction
import insyncwithfoo.ryecharm.unableToRunCommand
import insyncwithfoo.ryecharm.writeUnderAction
import kotlinx.coroutines.CoroutineScope


/**
 * Optimize imports for a given document (`Ctrl` + `Alt` + `O`).
 * 
 * @see RuffFormatter
 */
internal class RuffImportOptimizer : ImportOptimizer {
    
    override fun supports(file: PsiFile): Boolean {
        if (!file.isSupportedByRuff) {
            return false
        }
        
        return file.project.ruffConfigurations.run { executable != null && formatting && formatOnOptimizeImports }
    }
    
    override fun processFile(file: PsiFile) =
        file.makeProcessor() ?: Runnable {}
    
    private fun PsiFile.makeProcessor(): Runnable? {
        val ruff = project.ruff
        val document = viewProvider.document
        val path = virtualFile?.toNioPathOrNull()
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to create processor for file:
                |$virtualFile
                """.trimMargin()
            )
            return null
        }
        
        if (document == null) {
            project.unableToRunCommand(
                """
                |Was trying to create processor for file:
                |$virtualFile
                """.trimMargin()
            )
            return null
        }
        
        return Runnable {
            val command = ruff.optimizeImports(document.text, path)
            project.runCommandAndLoadResult(command, this)
        }
    }
    
    private fun Project.runCommandAndLoadResult(command: Command, file: PsiFile) = launch<Coroutine> {
        val output = runInBackground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            val title = message("progresses.command.ruff.optimizeImports")
            
            writeUnderAction<Coroutine>(title, file, newText)
        }
    }
    
    @Service(Service.Level.PROJECT)
    private class Coroutine(override val scope: CoroutineScope) : CoroutineService
    
}
