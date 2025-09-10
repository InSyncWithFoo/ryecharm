package insyncwithfoo.ryecharm.common.logging

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ConsoleViewPlace
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.message


private fun ConsoleView.log(message: String) {
    print(message, ConsoleViewContentType.NORMAL_OUTPUT)
}


internal class RyeCharmLoggingConsole(project: Project, private val tabName: String) :
    ConsoleViewImpl(project, viewer = false)
{
    
    override fun getPlace() = ConsoleViewPlace(RyeCharm.ID)
    
    override fun createConsoleActions(): Array<AnAction> {
        val toggleSoftWraps = ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE)
        val scrollToBottom = ScrollToTheEndToolbarAction(editor)
        val clearConsole = clearThisConsoleAction()
        
        return arrayOf(toggleSoftWraps, scrollToBottom, clearConsole)
    }
    
    /**
     * Call the super implementation and [printNotice].
     * 
     * This function gets called after [clear].
     */
    override fun doClear() {
        super.doClear()
        printNotice()
    }
    
    fun printNotice() {
        log(message("toolWindows.initializationMessage", tabName))
        log("\n\n")
    }
    
}
