package insyncwithfoo.ryecharm

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import java.nio.file.Path


internal val AnActionEvent.editor: Editor?
    get() = dataContext.getData(CommonDataKeys.EDITOR)


internal abstract class ShowCommandOutputAction : AnAction(), DumbAware {
    
    abstract fun createCommand(event: AnActionEvent, project: Project): Command?
    
    final override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: defaultProject
        val command = createCommand(event, project) ?: return
        
        project.runCommandAndShowOutput(command)
    }
    
    private fun Project.runCommandAndShowOutput(command: Command) = launch<ActionCoroutine> {
        runInBackground(command) { output ->
            notifyIfProcessIsUnsuccessfulOr(command, output) {
                showProcessResult(output.stdout)
            }
        }
    }
    
    @Service(Service.Level.PROJECT)
    private class ActionCoroutine(override val scope: CoroutineScope) : CoroutineService
    
}


internal abstract class ShowExecutableAction : AnAction(), DumbAware {
    
    abstract fun Project?.getExecutable(): Path?
    
    final override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        
        val message = when (val executable = project.getExecutable()) {
            null -> message("messages.showExecutable.body.notFound")
            else -> message("messages.showExecutable.body", executable)
        }
        
        project.showProcessResult(message)
    }
    
}
