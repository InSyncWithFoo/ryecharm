package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion

import com.intellij.codeInsight.hints.declarative.HintFormat
import com.intellij.codeInsight.hints.declarative.HintFontSize
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider
import com.intellij.codeInsight.hints.declarative.InlayTreeSink
import com.intellij.codeInsight.hints.declarative.InlineInlayPosition
import com.intellij.codeInsight.hints.declarative.OwnBypassCollector
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.endOffset
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.isUVToml
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.pep508Normalize
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.stringContent
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.uv
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings.DependencyVersionInlayHintsCustomSettingsProvider
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings.Settings
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversion.settings.dependencyVersionInlayHintsSettings
import insyncwithfoo.ryecharm.uv.parsePipListOutput
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlLiteral


private typealias DependencyName = String
private typealias DependencyVersion = String
private typealias Dependencies = Map<DependencyName, DependencyVersion>


private const val CONTINUE_PROCESSING = true


// https://peps.python.org/pep-0508/#names
private val dependencySpecifierLookAlike = """(?i)^\s*(?<name>[A-Z0-9](?:[A-Z0-9._-]*[A-Z0-9])?).*""".toRegex()


private class Collector : OwnBypassCollector {
    
    override fun collectHintsForFile(file: PsiFile, sink: InlayTreeSink) {
        val virtualFile = file.virtualFile ?: return
        
        if (file !is TomlFile || !virtualFile.isPyprojectToml && !virtualFile.isUVToml) {
            return
        }
        
        val project = file.project
        val uv = project.uv ?: return
        val dependencies = project.getInstalledDependencies(uv) ?: return
        
        val settings = dependencyVersionInlayHintsSettings
        
        PsiTreeUtil.processElements(file, TomlLiteral::class.java) { element ->
            if (shouldShowHint(element, virtualFile, settings)) {
                element.appendVersionHint(sink, dependencies)
            }
            CONTINUE_PROCESSING
        }
    }
    
    @Suppress("UnstableApiUsage")
    private fun Project.getInstalledDependencies(uv: UV): Dependencies? = runBlockingCancellable {
        val command = uv.pipList()
        val output = runInBackground(command)
        
        when {
            output.completedAbnormally -> null
            else -> parsePipListOutput(output.stdout)?.associate { it.name.pep508Normalize() to it.version }
        }
    }
    
    private fun shouldShowHint(element: TomlLiteral, file: VirtualFile, settings: Settings): Boolean {
        val string = element.takeIf { it.isString } ?: return false
        val array = string.parent as? TomlArray ?: return false
        val keyValuePair = array.keyValuePair ?: return false
        
        val key = keyValuePair.key
        val absoluteName = key.absoluteName
        
        return when {
            file.isPyprojectToml -> shouldShowHintForPyprojectTomlField(absoluteName, settings)
            else -> shouldShowHintForUVField(absoluteName, settings)
        }
    }
    
    private fun shouldShowHintForPyprojectTomlField(keyName: TOMLPath, settings: Settings): Boolean {
        when {
            keyName == TOMLPath("project.dependencies") -> return settings.projectDependencies
            keyName == TOMLPath("build-system.requires") -> return settings.buildSystemRequires
            keyName isChildOf "project.optional-dependencies" -> return settings.projectOptionalDependencies
            keyName isChildOf "dependency-groups" -> return settings.dependencyGroups
        }
        
        val relativeName = keyName.relativize("tool.uv") ?: return false
        
        return shouldShowHintForUVField(relativeName, settings)
    }
    
    private fun shouldShowHintForUVField(keyName: TOMLPath, settings: Settings) =
        when (keyName) {
            TOMLPath("constraint-dependencies") -> settings.uvConstraintDependencies
            TOMLPath("dev-dependencies") -> settings.uvDevDependencies
            TOMLPath("override-dependencies") -> settings.uvOverrideDependencies
            TOMLPath("upgrade-package") -> settings.uvUpgradePackage
            TOMLPath("pip.upgrade-package") -> settings.uvPipUpgradePackage
            else -> false
        }
    
    private fun TomlLiteral.appendVersionHint(sink: InlayTreeSink, dependencies: Dependencies) {
        val match = dependencySpecifierLookAlike.matchEntire(stringContent!!) ?: return
        val dependencyName = match.groups["name"]?.value?.pep508Normalize() ?: return
        
        val version = dependencies[dependencyName] ?: return
        
        sink.addVersionHint(version, endOffset)
    }
    
    private fun InlayTreeSink.addVersionHint(version: String, offset: Int) {
        val position = InlineInlayPosition(offset, relatedToPrevious = false)
        val hintFormat = HintFormat.default.withFontSize(HintFontSize.ABitSmallerThanInEditor)
        val tooltip = message("inlayHints.uv.dependencyVersions.tooltip", version)
        
        addPresentation(position, payloads = null, tooltip, hintFormat) {
            text(version)
        }
    }
    
}


/**
 * Provide version inlay hints for elements of:
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
 */
internal class DependencyVersionInlayHintsProvider : InlayHintsProvider, DumbAware {
    
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector =
        Collector()
    
}
