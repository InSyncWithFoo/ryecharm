package insyncwithfoo.ryecharm.ruff.intentions

import com.intellij.openapi.components.Service
import insyncwithfoo.ryecharm.CoroutineService
import kotlinx.coroutines.CoroutineScope


@Service(Service.Level.PROJECT)
internal class IntentionCoroutine(override val scope: CoroutineScope) : CoroutineService
