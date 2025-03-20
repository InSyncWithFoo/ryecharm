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


private fun TaskCancellation(cancellable: Boolean) = when (cancellable) {
    true -> TaskCancellation.cancellable()
    false -> TaskCancellation.nonCancellable()
}


private inline val Project.modalTaskOwner: ModalTaskOwner
    get() = ModalTaskOwner.project(this)


private fun ModalTaskOwner(project: Project?) =
    project?.modalTaskOwner ?: ModalTaskOwner.guess()


private suspend inline fun <T> withModalProgress(
    project: Project?,
    title: String,
    cancellable: Boolean,
    noinline action: suspend CoroutineScope.() -> T
) =
    withModalProgress(ModalTaskOwner(project), title, TaskCancellation(cancellable), action)


internal suspend inline fun <T> runInForeground(
    title: String,
    cancellable: Boolean,
    noinline action: suspend CoroutineScope.() -> T
) =
    withModalProgress(ModalTaskOwner.guess(), title, TaskCancellation(cancellable), action)


internal suspend inline fun <T> Project.runInForeground(
    title: String,
    cancellable: Boolean = true,
    noinline action: suspend CoroutineScope.() -> T
) =
    withModalProgress(this, title, cancellable, action)


internal suspend inline fun <T> Project.runInBackground(
    title: String,
    cancellable: Boolean = true,
    noinline action: suspend CoroutineScope.() -> T
) =
    withBackgroundProgress(this, title, cancellable, action)


@Suppress("UnstableApiUsage")
internal suspend inline fun <T> Project.runWriteCommandAction(title: String, noinline action: () -> T) =
    writeCommandAction(this, title, action)


/**
 * Run [action] with [Dispatchers.IO] as context.
 */
internal suspend inline fun <T> runUnderIOThread(noinline action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO, action)


/**
 * Run [action] with [Dispatchers.EDT] as context.
 */
internal suspend inline fun runUnderUIThread(noinline action: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.EDT, action)
}
