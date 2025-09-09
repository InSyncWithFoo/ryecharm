package insyncwithfoo.ryecharm.common.logging

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.openapi.wm.ex.ToolWindowManagerListener.ToolWindowManagerEventType
import com.intellij.ui.content.ContentFactory
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.messageBusConnection


private val contentFactory: ContentFactory
    get() = ContentFactory.getInstance()


private fun Project.onClose(listener: (Project) -> Unit) {
    messageBusConnection.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
        override fun projectClosing(project: Project) {
            listener(project)
        }
    })
}


private fun Project.onToolWindowHide(listener: (ToolWindowManager) -> Unit) {
    messageBusConnection.subscribe(ToolWindowManagerListener.TOPIC, object : ToolWindowManagerListener {
        override fun stateChanged(manager: ToolWindowManager, changeType: ToolWindowManagerEventType) {
            if (changeType == ToolWindowManagerEventType.HideToolWindow) {
                listener(manager)
            }
        }
    })
}


/**
 * Provide a tool window that displays commands run by the plugin
 * as well as their corresponding outputs.
 * 
 * Each tool has its own tab.
 */
internal class RyeCharmLoggingToolWindowFactory : ToolWindowFactory, DumbAware {
    
    override fun shouldBeAvailable(project: Project) = false
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        for (consoleKind in ConsoleHolderKind.entries) {
            val tabName = consoleKind.tabName
            
            val console = RyeCharmLoggingConsole(project, tabName)
            val contentTabCanBePinned = false
            val content = contentFactory.createContent(console.component, tabName, contentTabCanBePinned)
            
            toolWindow.contentManager.addContent(content)
            project.pluginLogger.register(consoleKind, console)
            
            console.printNotice()
        }
        
        project.onClose {
            Disposer.dispose(it.pluginLogger)
        }
        
        project.onToolWindowHide { manager ->
            if (!manager.isStripeButtonShow(toolWindow)) {
                toolWindow.isAvailable = false
            }
        }
    }
    
    companion object {
        const val ID = "${RyeCharm.ID}.common.logging.RyeCharmLoggingToolWindowFactory"
    }
    
}
