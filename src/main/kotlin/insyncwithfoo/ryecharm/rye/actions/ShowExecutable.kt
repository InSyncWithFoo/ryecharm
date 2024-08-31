package insyncwithfoo.ryecharm.rye.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import insyncwithfoo.ryecharm.configurations.globalRyeExecutable
import insyncwithfoo.ryecharm.configurations.ryeExecutable
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.showMessage


internal class ShowExecutable : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val executable = project?.ryeExecutable ?: globalRyeExecutable
        
        val message = when (executable) {
            null -> message("messages.showExecutable.body.notFound")
            else -> message("messages.showExecutable.body", executable)
        }
        
        project.showMessage(message)
    }
    
}
