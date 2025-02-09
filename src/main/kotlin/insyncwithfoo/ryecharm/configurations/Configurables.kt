package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.util.xmlb.XmlSerializerUtil
import insyncwithfoo.ryecharm.message


/**
 * Marker for project-based/local [Configurable]s.
 * 
 * Local configurables are different from global ones in that:
 * 
 * * They have [Overrides].
 * * They have corresponding [Project]s.
 * * Their panels' names are always "Project".
 * * Their panels are always children of the corresponding global panels.
 * 
 * @see AdaptivePanel
 */
internal interface ProjectBasedConfigurable : Configurable {
    
    val project: Project
    val overrides: Overrides
    
    override fun getDisplayName() = message("configurations.project.displayName")
    
}


/**
 * The base class from which concrete [Configurable] classes derive.
 * 
 * This class implements and delegates [isModified],
 * [reset] and [apply] to that of the [panel].
 * 
 * When the user is interacting with the UI panel,
 * there are three instances of the state [S],
 * each belong to a different holder class:
 * 
 * * One for the configurable which creates the panel (a subclass of this class)
 * * One for the panel itself (a subclass of [AdaptivePanel])
 * * One for the service (a subclass [ConfigurationService])
 * 
 * The panel's state stores whatever values that are shown in the UI.
 * The service's state stores whatever that will go to the `.xml` file.
 * The configurable's state acts as a medium between them.
 */
internal abstract class PanelBasedConfigurable<S : BaseState> : Configurable {
    
    protected abstract val state: S
    protected abstract val panel: DialogPanel
    
    override fun createComponent() = panel
    
    override fun isModified() = panel.isModified()
    
    override fun reset() {
        panel.reset()
    }
    
    override fun apply() {
        panel.apply()
        afterApply()
    }
    
    /**
     * Called from [apply] after [DialogPanel.apply].
     * 
     * Responsible for synchronizing the panel's state and
     * that of the service using [syncStateWithService].
     */
    protected abstract fun afterApply()
    
    /**
     * Copy the state of the panel to that of the service in-place.
     */
    protected fun <SS : BaseState> syncStateWithService(panelState: SS, serviceState: SS) {
        XmlSerializerUtil.copyBean(panelState, serviceState)
    }
    
}


/**
 * Shorthand to extract the [Project] and [Overrides]
 * from a [ProjectBasedConfigurable]:
 * 
 * ```kotlin
 * val (project, overrides) = configurable.projectAndOverrides
 * //            ^^^^^^^^^ Overrides?
 * //   ^^^^^^^ Project?
 * ```
 */
internal val <S : BaseState> PanelBasedConfigurable<S>.projectAndOverrides: Pair<Project?, Overrides?>
    get() = when (this is ProjectBasedConfigurable) {
        true -> Pair(project, overrides)
        else -> Pair(null, null)
    }
