package insyncwithfoo.ryecharm.rye.actions

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.ShowExecutableAction
import insyncwithfoo.ryecharm.configurations.globalRyeExecutable
import insyncwithfoo.ryecharm.configurations.ryeExecutable


internal class ShowExecutable : ShowExecutableAction(), DumbAware {
    
    override fun Project?.getExecutable() =
        this?.ryeExecutable ?: globalRyeExecutable
    
}
