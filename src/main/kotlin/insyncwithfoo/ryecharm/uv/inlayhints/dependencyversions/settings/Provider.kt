package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings

import com.intellij.codeInsight.hints.declarative.InlayHintsCustomSettingsProvider
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import insyncwithfoo.ryecharm.configurations.copy


internal class DependencyVersionInlayHintsCustomSettingsProvider : InlayHintsCustomSettingsProvider<Settings> {
    
    private val service = SettingsService.getInstance()
    
    private val state = service.state.copy()
    private val panel by lazy { createPanel(state) }
    
    override fun createComponent(project: Project, language: Language) = panel
    
    override fun getSettingsCopy() = state.copy()
    
    override fun isDifferentFrom(project: Project, settings: Settings) =
        state != settings
    
    override fun persistSettings(project: Project, settings: Settings, language: Language) {
        XmlSerializerUtil.copyBean(settings, service.state)
    }
    
    /**
     * This function is never called anywhere.
     * It appears to be a semi-deprecated version of [persistSettings].
     */
    override fun putSettings(project: Project, settings: Settings, language: Language) {}
    
}
