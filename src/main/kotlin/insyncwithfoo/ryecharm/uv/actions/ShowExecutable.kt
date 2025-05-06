package insyncwithfoo.ryecharm.uv.actions

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.ShowExecutableAction
import insyncwithfoo.ryecharm.configurations.globalUVExecutable
import insyncwithfoo.ryecharm.configurations.uvExecutable


internal class ShowExecutable : ShowExecutableAction(), DumbAware {
    
    override fun Project?.getExecutable() =
        this?.uvExecutable ?: globalUVExecutable
    
}
