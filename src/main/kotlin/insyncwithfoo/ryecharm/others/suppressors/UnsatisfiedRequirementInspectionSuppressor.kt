package insyncwithfoo.ryecharm.others.suppressors

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.python.requirements.inspections.tools.NotInstalledRequirementInspection
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.sdkIsUV
import insyncwithfoo.ryecharm.stringArrayTomlKey


/**
 * Suppress `UnsatisfiedRequirementInspection`
 * for `build-system.requires` in `pyproject.toml`
 * 
 * @see NotInstalledRequirementInspection
 */
internal class UnsatisfiedRequirementInspectionSuppressor : InspectionSuppressor, DumbAware {
    
    override fun getSuppressActions(element: PsiElement?, toolId: String) =
        emptyArray<SuppressQuickFix>()
    
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (toolId != TO_BE_SUPPRESSED) {
            return false
        }
        
        val (project, file) = Pair(element.project, element.containingFile.virtualFile)
        val configurations = project.mainConfigurations
        
        when {
            !file.isPyprojectToml -> return false
            !configurations.suppressIncorrectNIRI -> return false
            !configurations.suppressIncorrectNIRINonUVSDK && !project.sdkIsUV -> return false
        }
        
        val key = element.stringArrayTomlKey ?: return false
        
        return key.absoluteName == TOMLPath("build-system.requires")
    }
    
    companion object {
        private const val TO_BE_SUPPRESSED = "UnsatisfiedRequirement"
    }
    
}
