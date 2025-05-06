package insyncwithfoo.ryecharm.uv.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.ShowCommandOutputAction
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv


internal class ShowVersion : ShowCommandOutputAction() {
    
    override fun createCommand(event: AnActionEvent, project: Project): Command? {
        val uv = project.uv
        
        if (uv != null) {
            return uv.selfVersion()
        }
        
        project.couldNotConstructCommandFactory<UV>(
            """
            |Was trying to retrieve uv version.
            """.trimMargin()
        )
        return null
    }
    
}
