package insyncwithfoo.ryecharm.ruff.documentation.options

import com.intellij.model.Pointer
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.createSmartPointer
import insyncwithfoo.ryecharm.Definition
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.ruff.CachedResult
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toDocumentationResult
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.toml.TomlIcons
import org.toml.lang.psi.TomlKey


internal typealias OptionName = String
internal typealias OptionDocumentation = HTML


@Serializable
internal data class OptionDeprecationInfo(
    val since: String?,
    val message: String?
)


@Serializable
internal data class OptionInfo(
    val doc: String,
    val default: String,
    @SerialName("value_type")
    val valueType: String,
    val scope: String?,
    val example: String,
    val deprecated: OptionDeprecationInfo?
)


/**
 * Run `ruff config` and compute the documentation for [option]
 * or return the cached result of that when [element] is hovered.
 *
 * Verifications are done at [RuffOptionDocumentationTargetProvider].
 * 
 * @see [toHTML]
 */
@Suppress("UnstableApiUsage")
internal class RuffOptionDocumentationTarget(
    private val element: TomlKey,
    private val option: OptionName,
    private val fileName: String
) : DocumentationTarget {
    
    private fun OptionName.toAbsoluteName() =
        "ruff.$this"
    
    // This doesn't seem to do anything, despite being called.
    override fun computePresentation() =
        TargetPresentation.builder(fileName)
            .presentableText(fileName)
            .icon(TomlIcons.TomlFile)
            .presentation()
    
    override fun createPointer(): Pointer<out DocumentationTarget> {
        val elementPointer = element.createSmartPointer()
        
        return Pointer {
            elementPointer.dereference()?.let {
                RuffOptionDocumentationTarget(it, option, fileName)
            }
        }
    }
    
    /**
     * Return the syntax-highlighted TOML key name wrapped
     * in a [Definition] when the target is `Ctrl`-hovered.
     * 
     * The surrounding `<pre>` and `<code>` tags are removed
     * since they would otherwise introduce a surrounding box
     * similar to that of (inline) code blocks in IDEA's
     * KDoc renderings.
     * 
     * This also means that the result
     * will not be rendered in monospace font, which is
     * also the case with similar features but not KDoc.
     * This cannot be patched by prepending a [HtmlChunk.styleTag];
     * its content will simply be displayed as plain text.
     * 
     * The result of this method thus typically looks like
     * (formatted for readability):
     * 
     * ```html
     * <div class="definition">
     *   <span style="color:#cfa58f;">lint</span>
     *   <span style="">.</span>
     *   <span style="color:#cfa58f;">fixable</span>
     * </div>
     * ```
     * 
     * When the target is returned by a [DocumentationTargetProvider],
     * which [RuffOptionDocumentationTargetProvider] is,
     * this doesn't get called, possibly because
     * a non-PSI, non-symbol target cannot be link-highlighted.
     * 
     * It is only kept here for documentation purposes.
     * 
     * @see [RuffOptionDocumentationTargetProvider]
     */
    override fun computeDocumentationHint(): HTML {
        val html = option.toAbsoluteName().wrappedInCodeBlock("toml")
            .toHTML()
            .removeSurroundingTag("pre")
            .removeSurroundingTag("code")
        
        return Definition().apply { html(html) }.toString()
    }
    
    /**
     * Return the information about the configuration,
     * rendered as a documentation popup.
     * 
     * @see render
     */
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        element.project.getDocumentation(option)?.toDocumentationResult()
    }
    
    private suspend fun Project.getDocumentation(option: OptionName): OptionDocumentation? {
        val ruff = this.ruff ?: return null
        val executable = ruff.executable
        
        val cache = RuffCache.getInstance(this)
        val cached = cache.optionsDocumentation
        
        if (cached?.matches(executable) == true) {
            val documentation = cached.result[option]
            
            if (documentation != null) {
                return documentation
            }
        }
        
        val newData = getNewDocumentationData()?.also {
            cache.optionsDocumentation = CachedResult(it, executable)
        }
        
        return newData?.get(option)
    }
    
    private suspend fun Project.getNewDocumentationData(): Map<OptionName, OptionDocumentation>? {
        val command = ruff!!.config()
        val output = ProgressContext.IO.compute {
            runInBackground(command)
        }
        
        if (output.isTimeout) {
            processTimeout(command)
            return null
        }
        
        if (output.isCancelled || !output.isSuccessful) {
            return null
        }
        
        return readAction {
            parseAndConvertToHTML(output.stdout)
        }
    }
    
    private fun parseAndConvertToHTML(raw: String): Map<OptionName, OptionDocumentation>? {
        return parseConfigOutput(raw)?.mapValues { (name, info) ->
            info.render(name.toAbsoluteName())
        }
    }
    
    private fun parseConfigOutput(raw: String): Map<OptionName, OptionInfo>? {
        val json = Json { ignoreUnknownKeys = true }
        
        return try {
            json.decodeFromString<Map<OptionName, OptionInfo>>(raw)
        } catch (_: SerializationException) {
            null
        }
    }
    
}
