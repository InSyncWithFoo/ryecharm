package insyncwithfoo.ryecharm.others.requirementsinjection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.jetbrains.python.requirements.RequirementsLanguage
import com.jetbrains.python.requirements.injection.TomlRequirementsLanguageInjector
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.inject
import insyncwithfoo.ryecharm.isDependencySpecifierString
import insyncwithfoo.ryecharm.uv.inlayhints.dependencyversions.DependencyVersionInlayHintsProvider
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind


// Upstream issue: https://youtrack.jetbrains.com/issue/PY-71120
// https://github.com/JetBrains/intellij-community/blob/b653610871/python/src/com/jetbrains/python/requirements/injection/TomlRequirementsInjectionSupport.kt
/**
 * Inject *Requirements* fragments for known arrays of PEP 508 specifiers.
 * 
 * Since 2025.2, this class partially overlaps [TomlRequirementsLanguageInjector]
 * with regard to:
 * 
 * * `project.optional-dependencies.*`
 * * `dependency-groups.*`
 * * `build-system.requires`
 * * `tool.uv.dev-dependencies`
 * 
 * For some reason, `RequirementsInjectorTest` fails on 2025.2.4 but not 2025.3 EAP.
 * Debugging shows that only [TomlRequirementsLanguageInjector]'s results
 * are taken into account when calculating injections.
 * As a workaround, this class now implements [MultiHostInjector]
 * instead of [LanguageInjectionContributor] and has its `order` set to `first`.
 * 
 * @see isDependencySpecifierString
 * @see DependencyVersionInlayHintsProvider
 */
internal class RequirementsInjector : MultiHostInjector, DumbAware {  // TODO: Revert this on 2025.3
    
    override fun elementsToInjectIn() = listOf(TomlLiteral::class.java)
    
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val project = context.project
        val configurations = project.mainConfigurations
        
        if (!configurations.languageInjectionRequirements) {
            return
        }
        
        if (context !is TomlLiteral || !context.isDependencySpecifierString) {
            return
        }
        
        val string = context.kind as? TomlLiteralKind.String ?: return
        val valueRange = string.offsets.value ?: return
        
        val (prefix, suffix) = Pair("", "")
        val range = TextRange(valueRange.startOffset, valueRange.endOffset)
        
        registrar.inject(RequirementsLanguage.INSTANCE) {
            registrar.addPlace(prefix, suffix, context, range)
        }
    }
    
}
