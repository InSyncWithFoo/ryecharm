package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.util.xmlb.XmlSerializerUtil
import insyncwithfoo.ryecharm.message


internal interface ProjectBasedConfigurable : Configurable {
    
    val project: Project
    val overrides: Overrides
    
    override fun getDisplayName() = message("configurations.project.displayName")
    
}


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
    
    protected abstract fun afterApply()
    
    protected fun <SS : BaseState> syncStateWithService(panelState: SS, serviceState: SS) {
        XmlSerializerUtil.copyBean(panelState, serviceState)
    }
    
}


internal val <S : BaseState> PanelBasedConfigurable<S>.projectAndOverrides: Pair<Project?, Overrides?>
    get() = try {
        (this as ProjectBasedConfigurable)
            .run { Pair(project, overrides) }
    } catch (_: ClassCastException) {
        Pair(null, null)
    }
