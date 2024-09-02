package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.edit
import insyncwithfoo.ryecharm.message


@Suppress("ActionIsNotPreviewFriendly")
internal class RuffFixViolation(private val code: String, private val fix: Fix) : LocalQuickFix {
    
    override fun getFamilyName(): String {
        val (message, applicability) = Pair(fix.message, fix.applicability)
        
        // TODO: Use the same format as the server (?)
        //  > Ruff (A123): Message (applicability)
        return when (message) {
            null -> message("intentions.ruff.fixViolation.familyName.fallback")
            else -> message("intentions.ruff.fixViolation.familyName", code, message, applicability)
        }
    }
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        
        file.edit { document ->
            // FIXME: This might be unsafe, since offsets would change after each fix.
            fix.edits.forEach { edit ->
                document.performEdit(edit)
            }
        }
    }
    
}
