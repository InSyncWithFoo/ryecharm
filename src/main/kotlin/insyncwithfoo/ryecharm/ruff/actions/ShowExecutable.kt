package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.ShowExecutableAction
import insyncwithfoo.ryecharm.configurations.globalRuffExecutable
import insyncwithfoo.ryecharm.configurations.ruffExecutable


internal class ShowExecutable : ShowExecutableAction(), DumbAware {
    
    override fun Project?.getExecutable() =
        this?.ruffExecutable ?: globalRuffExecutable
    
}
