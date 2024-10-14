package insyncwithfoo.ryecharm.common.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElementVisitor
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.stringContent
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlInlineTable
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.TomlVisitor
import org.toml.lang.psi.ext.name


private class DependencyGroupNameVisitor(
    private val holder: ProblemsHolder,
    private val session: LocalInspectionToolSession
) : TomlVisitor() {
    
    private var knownGroupNames: List<String>?
        get() = session.getUserData(KEY)
        set(value) = session.putUserData(KEY, value)
    
    override fun visitTable(element: TomlTable) {
        registerKnownGroupNames(element)
    }
    
    private fun registerKnownGroupNames(table: TomlTable) {
        val file = table.containingFile
        
        when {
            file.virtualFile?.isPyprojectToml != true -> return
            table.header.key?.name != "dependency-groups" -> return
        }
        
        knownGroupNames = knownGroupNames ?: table.entries.mapNotNull { it.key.name }
    }
    
    override fun visitLiteral(element: TomlLiteral) {
        if (element.containingFile.virtualFile?.isPyprojectToml != true) {
            return
        }
        
        val string = element.takeIf { it.isString } ?: return
        val inlineTable = string.keyValuePair?.parent as? TomlInlineTable ?: return
        val array = inlineTable.parent as? TomlArray ?: return
        val keyValuePair = array.keyValuePair ?: return
        
        val groupName = string.stringContent ?: return
        val knownGroupNames = this.knownGroupNames ?: return
        
        when {
            !(keyValuePair.key.absoluteName isChildOf "dependency-groups") -> return
            groupName in knownGroupNames -> return
        }
        
        val message = message("inspections.dependencyGroupNames.message", groupName)
        val problemHighlightType = ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
    companion object {
        private const val KEY_NAME = "insyncwithfoo.ryecharm.common.inspection.DependencyGroupNameVisitor"
        private val KEY = Key.create<List<String>>(KEY_NAME)
    }
    
}


internal class DependencyGroupNameInspection : LocalInspectionTool(), DumbAware {
    
    override fun getShortName() = SHORT_NAME
    
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor =
        DependencyGroupNameVisitor(holder, session)
    
    companion object {
        const val SHORT_NAME = "insyncwithfoo.ryecharm.common.inspection.DependencyGroupNameInspection"
    }
    
}
