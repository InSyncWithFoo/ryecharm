package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection
import com.intellij.openapi.project.Project
import com.jetbrains.python.inspections.PyInspection
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.inspectionProfileManager


internal class RuffInspection : PyInspection(), ExternalAnnotatorBatchInspection {
    
    override fun getShortName() = SHORT_NAME
    
    companion object {
        const val SHORT_NAME = "${RyeCharm.ID}.ruff.linting.RuffInspection"
    }
    
}


internal var Project.ruffInspectionisEnabled: Boolean
    @Deprecated("The getter must not be used.", level = DeprecationLevel.ERROR)
    get() = throw RuntimeException()
    set(enabled) {
        val profile = inspectionProfileManager.currentProfile
        val toolState = profile.allTools.find { it.tool.shortName == RuffInspection.SHORT_NAME }
        
        toolState?.isEnabled = enabled
        profile.profileChanged()
    }
