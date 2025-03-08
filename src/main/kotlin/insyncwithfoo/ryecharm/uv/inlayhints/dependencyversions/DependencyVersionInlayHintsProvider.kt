package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions

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
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings.DependencyVersionInlayHintsCustomSettingsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.toDuration


private val DEPENDENCIES = Key.create<DependencyMap>("${RyeCharm.ID}.uv.inlayhints.dependencyversions")
private val LAST_UPDATED = Key.create<Instant>("${RyeCharm.ID}.uv.inlayhints.dependencyversions.timestamp")
private val RETRIEVING = Key.create<Boolean>("${RyeCharm.ID}.uv.inlayhints.dependencyversions.retrieving")


private inline var Editor.dependencies: DependencyMap?
    get() = getUserData(DEPENDENCIES)
    set(value) = putUserData(DEPENDENCIES, value)


private inline var Editor.lastUpdatedDependencies: Instant?
    get() = getUserData(LAST_UPDATED)
    set(value) = putUserData(LAST_UPDATED, value)


private inline var Editor.retrievingDependencies: Boolean
    get() = getUserData(RETRIEVING) ?: false
    set(value) = putUserData(RETRIEVING, value)


private inline fun <T> Editor.doRetrieval(retrieve: () -> T): T? {
    retrievingDependencies = true
    
    return try {
        retrieve()
    } catch (_: Throwable) {
        null
    } finally {
        retrievingDependencies = false
    }
}


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
 * @see DependencyVersionInlayHintsCollector
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
        
        if (editor.retrievingDependencies) {
            return null
        }
        
        val dependencies = editor.dependencies
        val lastUpdated = editor.lastUpdatedDependencies
        
        val cacheMaxAge = configurations.dependenciesDataMaxAge.toDuration(DurationUnit.SECONDS)
        val now = Clock.System.now()
        
        if (dependencies == null || lastUpdated == null || now - lastUpdated > cacheMaxAge) {
            project.collectDataAndTryAgainLater(editor, module?.interpreterPath)
            return null
        }
        
        return DependencyVersionInlayHintsCollector(dependencies)
    }
    
    private fun Project.collectDataAndTryAgainLater(editor: Editor, interpreter: Path?) = launch<Coroutine> {
        if (editor.retrievingDependencies) {
            return@launch
        }
        
        editor.dependencies = editor.doRetrieval {
            getInstalledDependencies(interpreter)
        }
        editor.lastUpdatedDependencies = Clock.System.now()
        
        DeclarativeInlayHintsPassFactory.scheduleRecompute(editor, this@collectDataAndTryAgainLater)
    }
    
}
