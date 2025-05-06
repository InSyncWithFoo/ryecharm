package insyncwithfoo.ryecharm.ruff.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.ShowCommandOutputAction
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.ruff


internal class ShowVersion : ShowCommandOutputAction() {
    
    override fun createCommand(event: AnActionEvent, project: Project): Command? {
        val ruff = project.ruff
        
        if (ruff != null) {
            return ruff.version()
        }
        
        project.couldNotConstructCommandFactory<Ruff>(
            """
            |Was trying to retrieve Ruff version.
            """.trimMargin()
        )
        return null
    }
    
}
