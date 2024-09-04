package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection
import com.intellij.openapi.project.Project
import com.jetbrains.python.inspections.PyInspection
import insyncwithfoo.ryecharm.inspectionProfileManager


internal class RuffInspection : PyInspection(), ExternalAnnotatorBatchInspection {
    
    override fun getShortName() = SHORT_NAME
    
    companion object {
        const val SHORT_NAME = "insyncwithfoo.ryecharm.ruff.linting.RuffInspection"
    }
    
}


internal var Project.ruffInspectionisEnabled: Boolean
    @Deprecated("The getter should not be used")
    get() = throw RuntimeException()
    set(enabled) {
        val profile = inspectionProfileManager.currentProfile
        val toolState = profile.allTools.find { it.tool.shortName == RuffInspection.SHORT_NAME }
        
        toolState?.isEnabled = enabled
        profile.profileChanged()
    }