package insyncwithfoo.ryecharm

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


private typealias Action = suspend CoroutineScope.() -> Unit


internal interface CoroutineService {
    val scope: CoroutineScope
}


private fun CoroutineService.launch(action: Action) {
    scope.launch(block = action)
}


internal inline fun <reified T : CoroutineService> Project.launch(noinline action: Action) {
    service<T>().launch(action)
}


internal inline fun <reified T : CoroutineService> launch(noinline action: Action) {
    service<T>().launch(action)
}
