package insyncwithfoo.ryecharm.ty.actions

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.ShowExecutableAction
import insyncwithfoo.ryecharm.configurations.globalTYExecutable
import insyncwithfoo.ryecharm.configurations.tyExecutable


internal class ShowExecutable : ShowExecutableAction(), DumbAware {
    
    override fun Project?.getExecutable() =
        this?.tyExecutable ?: globalTYExecutable
    
}
