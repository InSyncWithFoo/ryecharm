package insyncwithfoo.ryecharm.uv.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.message
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlVisitor


private class Visitor(private val holder: ProblemsHolder) : TomlVisitor() {
    
    override fun visitKey(element: TomlKey) {
        val file = element.containingFile.virtualFile ?: return
        val name = element.absoluteName
        
        when {
            file.isPyprojectToml && name == TOMLPath("tool.uv.dev-dependencies") -> report(element)
            file.isUVToml && name == TOMLPath("dev-dependencies") -> report(element)
        }
    }
    
    private fun report(element: TomlKey) {
        val message = message("inspections.uvDevDependencies.message")
        val problemHighlightType = ProblemHighlightType.WARNING
        
        holder.registerProblem(element, message, problemHighlightType)
    }
    
}


/**
 * Report usages of the `tool.uv.dev-dependencies` field.
 * 
 * This field is obsolete as of [uv 0.4.27](https://github.com/astral-sh/uv/releases/tag/0.4.27),
 * which added support for [PEP 735](https://peps.python.org/pep-0735/).
 */
internal class DevDependenciesInspection : LocalInspectionTool(), DumbAware {
    
    override fun getShortName() = SHORT_NAME
    
    override fun isAvailableForFile(file: PsiFile) =
        file.virtualFile?.run { isPyprojectToml || isUVToml } == true
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        Visitor(holder)
    
    companion object {
        private const val SHORT_NAME = "${RyeCharm.ID}.uv.inspections.DevDependenciesInspection"
    }
    
}
