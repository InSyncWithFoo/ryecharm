package insyncwithfoo.ryecharm.ruff.hierarchy

import com.intellij.ide.hierarchy.HierarchyBrowser
import com.intellij.ide.hierarchy.HierarchyProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.python.hierarchy.call.PyCallHierarchyBrowser
import com.jetbrains.python.hierarchy.call.PyCallHierarchyProvider
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.getRelevantElement
import insyncwithfoo.ryecharm.isNormalPyFile
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.project
import insyncwithfoo.ryecharm.ruff.commands.ruff


/**
 * Display the import hierarchy of modules.
 * 
 * This class is registered as a call hierarchy provider,
 * which is the most suitable out of the three (type, call, inheritance).
 */
internal class ImportGraphHierarchyProvider : HierarchyProvider {
    
    /**
     * Return the containing [PyFile] if and only if
     * the current element is a top-level [PsiWhiteSpace].
     * 
     * [PyCallHierarchyProvider] doesn't return null
     * when invoked on a [PsiWhiteSpace] child of a [PyFile],
     * even though [PyCallHierarchyBrowser] only expects functions
     * (judging by its constructor's parameter names).
     * 
     * @see PyCallHierarchyProvider.getTarget
     */
    override fun getTarget(dataContext: DataContext): PsiElement? {
        val project = dataContext.project ?: return null
        
        if (project.ruff == null || project.path == null) {
            return null
        }
        
        val file = when (val element = dataContext.getRelevantElement()) {
            is PsiWhiteSpace -> element.parent as? PyFile ?: return null
            is PyFile -> element
            else -> return null
        }
        
        if (!file.isNormalPyFile) {
            return null
        }
        
        return file
    }
    
    override fun createHierarchyBrowser(target: PsiElement) =
        ImportGraphBrowser(target as PyFile)
    
    /**
     * @see PyCallHierarchyProvider.browserActivated
     */
    override fun browserActivated(hierarchyBrowser: HierarchyBrowser) {
        (hierarchyBrowser as ImportGraphBrowser)
            .changeView(ImportGraphBrowser.importersOf)
    }
    
}
