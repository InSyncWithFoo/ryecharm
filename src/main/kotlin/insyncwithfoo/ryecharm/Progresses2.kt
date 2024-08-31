@file:Suppress("UsagesOfObsoleteApi")

package insyncwithfoo.ryecharm

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.ModalTaskOwner
import insyncwithfoo.ryecharm.configurations.HasTimeouts


private val Project.modalTaskOwner: ModalTaskOwner
    get() = ModalTaskOwner.project(this)


private fun Task.Backgroundable.run() {
    ProgressManager.getInstance().run(this)
}


private fun Project.backgroundableTask(title: String, action: () -> Unit) =
    object : Task.Backgroundable(this, title, false) {
        override fun run(indicator: ProgressIndicator) {
            indicator.text = title
            indicator.isIndeterminate = true
            action()
        }
    }


internal fun Project.runBackgroundableTask(title: String, action: () -> Unit) {
    backgroundableTask(title, action).run()
}


private fun Project.getCommandTimeout(command: Command) =
    (command as? CommandWithTimeout)?.getTimeout(this) ?: HasTimeouts.NO_LIMIT


internal fun Project.runBackgroundableTask(command: Command) {
    val title = command.runningMessage
    val timeout = getCommandTimeout(command)
    
    runBackgroundableTask(title) {
        command.run(timeout)
    }
}
