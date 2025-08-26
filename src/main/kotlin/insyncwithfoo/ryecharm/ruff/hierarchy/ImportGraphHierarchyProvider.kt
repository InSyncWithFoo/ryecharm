package insyncwithfoo.ryecharm.ruff.hierarchy

import com.intellij.ide.hierarchy.HierarchyBrowser
import com.intellij.ide.hierarchy.HierarchyProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.psi.PsiElement
import com.jetbrains.python.hierarchy.call.PyCallHierarchyProvider
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
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
     * Return the current element if and only if
     * it is a normal [PyFile].
     * 
     * @see isNormalPyFile
     * @see PyCallHierarchyProvider.getTarget
     */
    override fun getTarget(dataContext: DataContext): PsiElement? {
        val project = dataContext.project ?: return null
        val configurations = project.ruffConfigurations
        
        if (project.ruff == null || project.path == null) {
            return null
        }
        
        if (!configurations.showImportGraphOnCallHierarchyForFile) {
            return null
        }
        
        val file = dataContext.getRelevantElement() as? PyFile ?: return null
        
        if (!file.isNormalPyFile) {
            return null
        }
        
        return file
    }
    
    override fun createHierarchyBrowser(target: PsiElement) =
        ImportGraphBrowser(target as PyFile)
    
    override fun browserActivated(hierarchyBrowser: HierarchyBrowser) {
        (hierarchyBrowser as ImportGraphBrowser)
            .changeView(ImportGraphBrowser.importersOf)
    }
    
}
