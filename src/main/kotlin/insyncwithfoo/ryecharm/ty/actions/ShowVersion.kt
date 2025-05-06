package insyncwithfoo.ryecharm.ty.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.ShowCommandOutputAction
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.ty.commands.TY
import insyncwithfoo.ryecharm.ty.commands.ty


internal class ShowVersion : ShowCommandOutputAction() {
    
    override fun createCommand(event: AnActionEvent, project: Project): Command? {
        val ty = project.ty
        
        if (ty != null) {
            return ty.version()
        }
        
        project.couldNotConstructCommandFactory<TY>(
            """
            |Was trying to retrieve ty version.
            """.trimMargin()
        )
        return null
    }
    
}
