package insyncwithfoo.ryecharm.common.logging

import com.intellij.ide.actions.ActivateToolWindowAction
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.OtherIcons
import insyncwithfoo.ryecharm.message


internal class ActivateRyeCharmLoggingToolWindowAction :
    ActivateToolWindowAction(RyeCharmLoggingToolWindowFactory.ID)
{
    
    init {
        @Suppress("DialogTitleCapitalization")
        templatePresentation.text = message("toolwindow.stripe.${RyeCharmLoggingToolWindowFactory.ID}")
        templatePresentation.icon = OtherIcons.SHOW_LOGS_13
    }
    
    override fun hasEmptyState(project: Project) = true
    
}
