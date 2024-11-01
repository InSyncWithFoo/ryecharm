package insyncwithfoo.ryecharm.others.dependencygroups

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.stringContent
import insyncwithfoo.ryecharm.table
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlInlineTable
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.TomlVisitor
import org.toml.lang.psi.ext.name


private class Visitor(private val holder: ProblemsHolder) : TomlVisitor() {
    
    override fun visitLiteral(element: TomlLiteral) {
        val string = element.takeIf { it.isString } ?: return
        val propertyPair = string.keyValuePair?.takeIf { it.isIncludeGroup } ?: return
        
        val inlineTable = propertyPair.parent as? TomlInlineTable ?: return
        val array = inlineTable.parent as? TomlArray ?: return
        val arrayKey = array.keyValuePair?.key ?: return
        
        val dependencyGroupsTable = arrayKey.table?.takeIf { it.isDependencyGroupsTable }
        val registeredGroupNames = dependencyGroupsTable?.groupNames ?: emptyList()
        val groupName = string.stringContent ?: return
        val normalizedGroupName = groupName.normalize()
        
        when {
            !groupName.isValid -> reportInvalidGroupName(string, groupName)
            normalizedGroupName !in registeredGroupNames -> reportUnknownGroup(string, groupName, normalizedGroupName)
            normalizedGroupName == arrayKey.groupName -> reportCircularGroup(string, groupName, normalizedGroupName)
        }
    }
    
    private fun reportInvalidGroupName(element: PsiElement, groupName: String) {
        val message = message("inspections.dependencyGroupNames.message.invalid", groupName)
        val problemHighlightType = ProblemHighlightType.WARNING
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
    private fun reportUnknownGroup(element: PsiElement, originalName: String, normalizedName: String) {
        val message = message("inspections.dependencyGroupNames.message.unknown", originalName, normalizedName)
        val problemHighlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
    private fun reportCircularGroup(element: PsiElement, originalName: String, normalizedName: String) {
        val message = message("inspections.dependencyGroupNames.message.circular", originalName, normalizedName)
        val problemHighlightType = ProblemHighlightType.GENERIC_ERROR
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
    override fun visitTable(element: TomlTable) {
        val dependencyGroupsTable = element.takeIf { it.isDependencyGroupsTable } ?: return
        val entriesByNormalizedName = dependencyGroupsTable.entries.groupBy { it.key.groupName }
        
        entriesByNormalizedName.forEach { (normalizedName, entries) ->
            if (normalizedName != null && entries.size > 1) {
                entries.forEach { reportDuplicateGroup(it.key, it.key.name!!, normalizedName) }
            }
        }
    }
    
    private fun reportDuplicateGroup(element: PsiElement, originalName: String, normalizedName: String) {
        val message = message("inspections.dependencyGroupNames.message.duplicate", originalName, normalizedName)
        val problemHighlightType = ProblemHighlightType.GENERIC_ERROR
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
}


internal class DependencyGroupInspection : LocalInspectionTool(), DumbAware {
    
    override fun getShortName() = SHORT_NAME
    
    override fun isAvailableForFile(file: PsiFile) =
        file.virtualFile?.isPyprojectToml == true
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        Visitor(holder)
    
    companion object {
        private const val SHORT_NAME = "insyncwithfoo.ryecharm.others.dependencygroups.DependencyGroupInspection"
    }
    
}
