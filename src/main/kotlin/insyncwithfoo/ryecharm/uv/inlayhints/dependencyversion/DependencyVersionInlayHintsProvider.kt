package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion

import com.intellij.codeInsight.hints.declarative.InlayHintsCollector
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider
import com.intellij.codeInsight.hints.declarative.impl.DeclarativeInlayHintsPassFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import insyncwithfoo.ryecharm.CoroutineService
import insyncwithfoo.ryecharm.RyeCharm
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.interpreterPath
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.module
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings.DependencyVersionInlayHintsCustomSettingsProvider
import kotlinx.coroutines.CoroutineScope
import java.nio.file.Path


private val DEPENDENCIES = Key.create<DependencyMap>("${RyeCharm.ID}.uv.inlayhints.dependencyversion")
private val CHANGED = Key.create<Boolean>("${RyeCharm.ID}.uv.inlayhints.dependencyversion.rendered")


@Service(Service.Level.PROJECT)
private class Coroutine(override val scope: CoroutineScope) : CoroutineService


/**
 * Display currently installed versions of dependencies
 * next to dependency specifier strings.
 * 
 * Elements of the following arrays are recognized:
 *
 * * `project.dependencies`
 * * `project.optional-dependencies.*`
 * * `build-system.requires`
 * * `dependency-groups.*`
 * * `uv.constraint-dependencies`
 * * `uv.dev-dependencies`
 * * `uv.override-dependencies`
 * * `uv.upgrade-package`
 * * `uv.pip.upgrade-package`
 *
 * Each of the aforementioned can be disabled
 * via [DependencyVersionInlayHintsCustomSettingsProvider].
 * 
 * @see Collector
 */
internal class DependencyVersionInlayHintsProvider : InlayHintsProvider, DumbAware {
    
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {
        if (file.virtualFile?.run { isPyprojectToml || isUVToml } != true) {
            return null
        }
        
        val (project, module) = Pair(file.project, file.module)
        val configurations = project.uvConfigurations
        
        if (configurations.retrieveDependenciesInReadAction) {
            return DependencyVersionInlayHintsCollector()
        }
        
        val dependencies = editor.getUserData(DEPENDENCIES)
        val changed = editor.getUserData(CHANGED)
        
        if (dependencies == null || changed == false) {
            project.collectDataAndTryAgainLater(editor, module?.interpreterPath)
            return null
        }
        
        editor.putUserData(CHANGED, false)
        
        return DependencyVersionInlayHintsCollector(dependencies)
    }
    
    private fun Project.collectDataAndTryAgainLater(editor: Editor, interpreter: Path?) = launch<Coroutine> {
        val dependencies = getInstalledDependencies(interpreter)
        
        editor.putUserData(DEPENDENCIES, dependencies)
        editor.putUserData(CHANGED, true)
        
        DeclarativeInlayHintsPassFactory.scheduleRecompute(editor, this@collectDataAndTryAgainLater)
    }
    
}
