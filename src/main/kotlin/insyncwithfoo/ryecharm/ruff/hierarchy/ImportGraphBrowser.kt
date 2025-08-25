package insyncwithfoo.ryecharm.ruff.hierarchy

import com.intellij.icons.AllIcons
import com.intellij.ide.hierarchy.CallHierarchyBrowserBase
import com.intellij.ide.hierarchy.HierarchyBrowserBaseEx
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.PopupHandler
import com.jetbrains.python.hierarchy.PyHierarchyNodeDescriptor
import com.jetbrains.python.hierarchy.PyHierarchyUtils
import com.jetbrains.python.hierarchy.call.PyCallHierarchyBrowser
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.actionManager
import insyncwithfoo.ryecharm.invokeLater
import insyncwithfoo.ryecharm.isNormalPyFile
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.ruff.commands.AnalyzeGraphDirection
import org.jetbrains.annotations.Nls
import java.util.function.Supplier
import javax.swing.Icon
import javax.swing.JTree


/**
 * @see PyCallHierarchyBrowser
 */
internal class ImportGraphBrowser(file: PyFile) : CallHierarchyBrowserBase(file.project, file), DumbAware {
    
    private val project = file.project
    
    /**
     * @see CallHierarchyBrowserBase.prependActions
     */
    override fun prependActions(actionGroup: DefaultActionGroup) {
        val changeToImportersView = ChangeViewTypeAction(
            type = importersOf,
            text = message("hierarchies.ruff.importGraph.changeView.importers.text"),
            description = message("hierarchies.ruff.importGraph.changeView.importers.description"),
            icon = AllIcons.Hierarchy.Supertypes
        )
        val changeToImporteesView = ChangeViewTypeAction(
            type = importeesOf,
            text = message("hierarchies.ruff.importGraph.changeView.importees.text"),
            description = message("hierarchies.ruff.importGraph.changeView.importees.description"),
            icon = AllIcons.Hierarchy.Subtypes
        )
        
        actionGroup.add(changeToImportersView)
        actionGroup.add(changeToImporteesView)
        actionGroup.add(AlphaSortAction())
        actionGroup.add(ChangeScopeAction())
    }
    
    override fun getPrevOccurenceActionNameImpl() =
        message("hierarchies.ruff.importGraph.previousOccurence.text")
    
    override fun getNextOccurenceActionNameImpl() =
        message("hierarchies.ruff.importGraph.nextOccurence.text")
    
    override fun getPresentableNameMap() = mapOf(
        importersOf to Supplier { importersOf },
        importeesOf to Supplier { importeesOf }
    )
    
    override fun getElementFromDescriptor(descriptor: HierarchyNodeDescriptor) =
        (descriptor as? PyHierarchyNodeDescriptor)?.psiElement
    
    override fun createTrees(trees: MutableMap<in @Nls String, in JTree>) {
        val group = actionManager.getAction(IdeActions.GROUP_CALL_HIERARCHY_POPUP) as ActionGroup
        
        trees[importersOf] = createHierarchyTree(group)
        trees[importeesOf] = createHierarchyTree(group)
    }
    
    private fun createHierarchyTree(group: ActionGroup): JTree {
        val dndAware = false
        
        return createTree(dndAware).also {
            PopupHandler.installPopupMenu(it, group, ActionPlaces.CALL_HIERARCHY_VIEW_POPUP)
        }
    }
    
    override fun isApplicableElement(element: PsiElement) =
        element is PsiFile && element.isNormalPyFile
    
    override fun createHierarchyTreeStructure(type: String, psiElement: PsiElement) =
        createHierarchyTreeStructure(type, psiElement as PyFile)
    
    private fun createHierarchyTreeStructure(type: String, file: PyFile) = when (type) {
        importersOf -> ImportGraphTreeStructure(file, currentScopeType, direction = AnalyzeGraphDirection.DEPENDENTS)
        importeesOf -> ImportGraphTreeStructure(file, currentScopeType, direction = AnalyzeGraphDirection.DEPENDENCIES)
        else -> null.also { thisLogger().error("Unexpected type: $type") }
    }
    
    override fun getComparator() =
        PyHierarchyUtils.getComparator(project)
    
    override fun getActionUpdateThread() = ActionUpdateThread.EDT
    
    private inner class ChangeViewTypeAction(private val type: String, text: String, description: String, icon: Icon) :
        ToggleAction(text, description, icon), DumbAware
    {
        
        override fun getActionUpdateThread() = ActionUpdateThread.BGT
        
        override fun isSelected(event: AnActionEvent): Boolean {
            val currentType = event.updateSession.compute(this, "getCurrentViewType", ActionUpdateThread.EDT) {
                getCurrentViewType()
            }
            
            return type == currentType
        }
        
        override fun setSelected(event: AnActionEvent, state: Boolean) {
            if (state) {
                invokeLater { changeView(type) }
            }
        }
        
        override fun update(event: AnActionEvent) {
            super.update(event)
            
            val isValidBase = HierarchyBrowserBaseEx::class.java.getDeclaredMethod("isValidBase")
            isValidBase.isAccessible = true
            
            setEnabled(isValidBase.invoke(this@ImportGraphBrowser) as Boolean)
        }
        
    }
    
    companion object {
        inline val importersOf: @Nls String
            get() = message("hierarchies.ruff.importGraph.importersOf", "{0}")
        
        inline val importeesOf: @Nls String
            get() = message("hierarchies.ruff.importGraph.importeesOf", "{0}")
    }
    
}
