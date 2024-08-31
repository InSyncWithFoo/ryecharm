package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import insyncwithfoo.ryecharm.openProjects
import insyncwithfoo.ryecharm.ruff.RuffCache


internal class ClearPluginCache : AnAction(), DumbAware {
    
    override fun actionPerformed(event: AnActionEvent) {
        openProjects.forEach {
            RuffCache.getInstance(it).clear()
        }
    }
    
}
