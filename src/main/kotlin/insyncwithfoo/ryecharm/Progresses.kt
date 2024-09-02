package insyncwithfoo.ryecharm

import com.intellij.openapi.application.EDT
import com.intellij.openapi.command.writeCommandAction
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.TaskCancellation
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.ide.progress.withModalProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


private fun TaskCancellation(cancellable: Boolean) = when (cancellable) {
    true -> TaskCancellation.cancellable()
    false -> TaskCancellation.nonCancellable()
}


private val Project.modalTaskOwner: ModalTaskOwner
    get() = ModalTaskOwner.project(this)


private fun ModalTaskOwner(project: Project?) =
    project?.modalTaskOwner ?: ModalTaskOwner.guess()


private suspend fun <T> withModalProgress(
    project: Project?,
    title: String,
    cancellable: Boolean,
    action: suspend CoroutineScope.() -> T
) =
    withModalProgress(ModalTaskOwner(project), title, TaskCancellation(cancellable), action)


internal suspend fun <T> runInForeground(
    title: String,
    cancellable: Boolean,
    action: suspend CoroutineScope.() -> T
) =
    withModalProgress(ModalTaskOwner.guess(), title, TaskCancellation(cancellable), action)


internal suspend fun <T> Project.runInForeground(
    title: String,
    cancellable: Boolean = true,
    action: suspend CoroutineScope.() -> T
) =
    withModalProgress(this, title, cancellable, action)


internal suspend fun <T> Project.runInBackground(
    title: String,
    cancellable: Boolean = true,
    action: suspend CoroutineScope.() -> T
) =
    withBackgroundProgress(this, title, cancellable, action)


@Suppress("UnstableApiUsage")
internal suspend fun <T> Project.runWriteCommandAction(title: String, action: () -> T) =
    writeCommandAction(this, title, action)


internal enum class ProgressContext(private val context: CoroutineContext) {
    IO(Dispatchers.IO),
    UI(Dispatchers.EDT);
    
    suspend fun <T> compute(action: suspend CoroutineScope.() -> T) =
        withContext(context, action)
    
    suspend fun launch(action: suspend CoroutineScope.() -> Unit) {
        compute(action)
    }
}
