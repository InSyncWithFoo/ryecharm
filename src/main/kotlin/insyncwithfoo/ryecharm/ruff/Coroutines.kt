package insyncwithfoo.ryecharm.ruff

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Service(Service.Level.PROJECT)
private class FormattingCoroutine(val scope: CoroutineScope)


internal fun Project.runFormattingOperation(action: suspend CoroutineScope.() -> Unit) {
    service<FormattingCoroutine>().scope.launch(block = action)
}
