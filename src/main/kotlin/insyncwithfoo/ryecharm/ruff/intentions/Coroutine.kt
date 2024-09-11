package insyncwithfoo.ryecharm.ruff.intentions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Service(Service.Level.PROJECT)
private class IntentionCoroutine(val scope: CoroutineScope)


internal fun Project.runIntention(action: suspend CoroutineScope.() -> Unit) {
    service<IntentionCoroutine>().scope.launch(block = action)
}
