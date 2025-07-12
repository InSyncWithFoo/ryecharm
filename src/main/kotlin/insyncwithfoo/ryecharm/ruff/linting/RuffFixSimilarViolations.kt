package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.ExternalQuickFix
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.fix
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.writeUnderAction
import kotlinx.coroutines.CoroutineScope


@Service(Service.Level.PROJECT)
private class Coroutine(override val scope: CoroutineScope) : CoroutineService


internal class RuffFixSimilarViolations(private val code: RuleCode, private val unsafe: Boolean) : ExternalQuickFix {
    
    override fun getFamilyName(): String {
        val note = when (unsafe) {
            true -> message("intentions.ruff.fixSimilarViolations.unsafe")
            else -> message("intentions.ruff.fixSimilarViolations.safe")
        }
        
        return message("intentions.ruff.fixSimilarViolations.familyName", code, note)
    }
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile ?: return
        val document = file.viewProvider.document
        
        val path = file.virtualFile?.toNioPathOrNull()
        val ruff = project.ruff
        
        if (ruff == null) {
            project.couldNotConstructCommandFactory<Ruff>(
                """
                |Was trying to fix $code violations:
                |$path
                """.trimMargin()
            )
            return
        }
        
        val command = ruff.fix(document.text, path, rules = listOf(code), unsafe = unsafe)
        
        project.runCommandAndLoadResult(command, file)
    }
    
    private fun Project.runCommandAndLoadResult(command: Command, file: PsiFile) = launch<Coroutine> {
        val output = runInForeground(command)
        val newText = output.stdout
        
        notifyIfProcessIsUnsuccessfulOr(command, output) {
            val title = message("progresses.command.ruff.fix")
            
            writeUnderAction<Coroutine>(title, file, newText)
        }
    }
    
}
