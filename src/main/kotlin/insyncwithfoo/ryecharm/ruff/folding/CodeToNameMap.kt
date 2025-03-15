package insyncwithfoo.ryecharm.ruff.folding

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.RuleName
import insyncwithfoo.ryecharm.ruff.documentation.getRuleNameToCodeMap
import kotlinx.coroutines.CoroutineScope


@Service(Service.Level.PROJECT)
internal class Coroutine(override val scope: CoroutineScope) : CoroutineService


internal fun Project.getCodeToNameMapOrTriggerRetrieving(): Map<RuleCode, RuleName>? {
    val cache = RuffCache.getInstance(this)
    val nameToCodeMap = cache.ruleNameToCodeMap?.result
    
    if (nameToCodeMap == null) {
        launch<Coroutine> { getRuleNameToCodeMap() }
        return null
    }
    
    return nameToCodeMap.entries.associate { it.value to it.key }
}
