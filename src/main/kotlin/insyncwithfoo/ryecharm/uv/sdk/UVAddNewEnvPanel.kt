package insyncwithfoo.ryecharm.uv.sdk

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.sdk.add.PyAddNewEnvPanel
import insyncwithfoo.ryecharm.message


/**
 * @see UVSDKProvider.createNewEnvironmentPanel
 */
@Suppress("UnstableApiUsage")
internal class UVAddNewEnvPanel : PyAddNewEnvPanel() {
    
    override val envName = message("newProjectPanel.title")
    override val panelName = message("newProjectPanel.title")
    
    init {
        add(createComponent())
    }
    
    private fun createComponent() = panel {
        row {
            text(message("newProjectPanel.intellijIDEAIsNotSupported"))
        }
    }
    
    override fun validateAll() =
        listOf(ValidationInfo(message("newProjectPanel.intellijIDEAIsNotSupported")))
    
}
