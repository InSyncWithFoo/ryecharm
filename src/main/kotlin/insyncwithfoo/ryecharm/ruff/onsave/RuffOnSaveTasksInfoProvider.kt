package insyncwithfoo.ryecharm.ruff.onsave

import com.intellij.ide.actionsOnSave.ActionOnSaveBackedByOwnConfigurable
import com.intellij.ide.actionsOnSave.ActionOnSaveContext
import com.intellij.ide.actionsOnSave.ActionOnSaveInfo
import com.intellij.ide.actionsOnSave.ActionOnSaveInfoProvider
import insyncwithfoo.ryecharm.configurations.ruff.RuffProjectConfigurable
import insyncwithfoo.ryecharm.message


private const val CONFIGURABLE_ID = "insyncwithfoo.ryecharm.configurations.ruff.Local"


// TODO: Implement this fully (?)
private class RuffOnSaveTaskInfo(context: ActionOnSaveContext, private val displayName: String) :
    ActionOnSaveBackedByOwnConfigurable<RuffProjectConfigurable>(
        context,
        CONFIGURABLE_ID,
        RuffProjectConfigurable::class.java
    )
{
    
    override fun getActionOnSaveName() = displayName
    
    override fun isApplicableAccordingToUiState(configurable: RuffProjectConfigurable) = false
    override fun isApplicableAccordingToStoredState() = false
    override fun setActionOnSaveEnabled(configurable: RuffProjectConfigurable, enabled: Boolean) {}
    
    override fun getActionLinks() = listOf(createGoToPageInSettingsLink(CONFIGURABLE_ID))
    
}


internal class RuffOnSaveTasksInfoProvider : ActionOnSaveInfoProvider() {
    
    override fun getActionOnSaveInfos(context: ActionOnSaveContext): Collection<ActionOnSaveInfo> =
        listOf(
            RuffOnSaveTaskInfo(context, message("actionsOnSave.ruffFormat.displayName")),
            RuffOnSaveTaskInfo(context, message("actionsOnSave.ruffFix.displayName"))
        )
    
}
