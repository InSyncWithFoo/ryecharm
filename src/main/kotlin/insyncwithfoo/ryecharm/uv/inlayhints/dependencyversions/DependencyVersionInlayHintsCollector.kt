package insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions

import com.intellij.codeInsight.hints.declarative.HintFontSize
import com.intellij.codeInsight.hints.declarative.HintFormat
import com.intellij.codeInsight.hints.declarative.InlayTreeSink
import com.intellij.codeInsight.hints.declarative.InlineInlayPosition
import com.intellij.codeInsight.hints.declarative.OwnBypassCollector
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.util.endOffset
import com.jetbrains.python.packaging.common.PythonPackage
import insyncwithfoo.ryecharm.TOMLPath
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.dependencySpecifierLookAlike
import insyncwithfoo.ryecharm.interpreterPath
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.module
import insyncwithfoo.ryecharm.pep508Normalize
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.stringContent
import insyncwithfoo.ryecharm.traverse
import insyncwithfoo.ryecharm.uv.commands.uv
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings.Settings
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.settings.dependencyVersionInlayHintsSettings
import insyncwithfoo.ryecharm.uv.parsePipListOutput
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlLiteral
import java.nio.file.Path


internal typealias DependencyName = String
internal typealias DependencyVersion = String
internal typealias DependencyMap = Map<DependencyName, DependencyVersion>
internal typealias DependencyList = List<PythonPackage>


private fun DependencyList.toMap(): DependencyMap =
    this.associate { it.name.pep508Normalize() to it.version }


internal suspend fun Project.getInstalledDependencies(interpreter: Path?): DependencyMap? {
    val uv = this.uv ?: return null
    val command = uv.pipList(python = interpreter)
    
    val output = runInBackground(command)
    
    return when {
        output.completedAbnormally -> null
        else -> parsePipListOutput(output.stdout)?.toMap()
    }
}


internal class DependencyVersionInlayHintsCollector(private val dependencies: DependencyMap?) : OwnBypassCollector {
    
    constructor() : this(dependencies = null)
    
    override fun collectHintsForFile(file: PsiFile, sink: InlayTreeSink) {
        if (file !is TomlFile) {
            return
        }
        
        val virtualFile = file.virtualFile ?: return
        val dependencies = this.dependencies ?: getInstalledDependencies(file) ?: return
        val settings = dependencyVersionInlayHintsSettings
        
        file.traverse<TomlLiteral> { element ->
            if (shouldShowHint(element, virtualFile, settings)) {
                sink.addVersionHint(element, dependencies)
            }
        }
    }
    
    @Suppress("UnstableApiUsage")
    private fun getInstalledDependencies(file: PsiFile) = runBlockingCancellable {
        val (project, module) = Pair(file.project, file.module)
        project.getInstalledDependencies(module?.interpreterPath)
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
    
    private fun InlayTreeSink.addVersionHint(element: TomlLiteral, dependencies: DependencyMap) {
        val match = dependencySpecifierLookAlike.matchEntire(element.stringContent!!) ?: return
        val dependencyName = match.groups["name"]?.value?.pep508Normalize() ?: return
        val dependencyVersion = dependencies[dependencyName] ?: return
        
        addVersionHint(dependencyName, dependencyVersion, element.endOffset)
    }
    
    private fun InlayTreeSink.addVersionHint(name: DependencyName, version: DependencyVersion, offset: Int) {
        val position = InlineInlayPosition(offset, relatedToPrevious = false)
        val hintFormat = HintFormat.default.withFontSize(HintFontSize.ABitSmallerThanInEditor)
        val tooltip = message("inlayHints.uv.dependencyVersions.tooltip", name, version)
        
        addPresentation(position, payloads = null, tooltip, hintFormat) {
            text(version)
        }
    }
    
}
