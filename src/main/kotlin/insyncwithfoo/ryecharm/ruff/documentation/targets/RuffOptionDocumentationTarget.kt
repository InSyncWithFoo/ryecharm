package insyncwithfoo.ryecharm.ruff.documentation.targets

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.Definition
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.parseAsJSONLeniently
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.removeSurroundingTag
import insyncwithfoo.ryecharm.ruff.CachedResult
import insyncwithfoo.ryecharm.ruff.RuffCache
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.ruff.documentation.OptionDocumentation
import insyncwithfoo.ryecharm.ruff.documentation.OptionInfo
import insyncwithfoo.ryecharm.ruff.documentation.OptionName
import insyncwithfoo.ryecharm.ruff.documentation.providers.RuffOptionDocumentationTargetProvider
import insyncwithfoo.ryecharm.ruff.documentation.render
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toDocumentationResult
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.wrappedInCodeBlock


/**
 * Run `ruff config` and compute the documentation for [option]
 * or return the cached result of that when [element] is hovered.
 *
 * Verifications are done at [RuffOptionDocumentationTargetProvider].
 * 
 * @see render
 */
internal class RuffOptionDocumentationTarget(
    override val element: PsiElement,
    private val option: OptionName
) : RuffDocumentationTarget() {
    
    override fun fromDereferenced(element: PsiElement) =
        RuffOptionDocumentationTarget(element, option)
    
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
    
    private fun OptionName.toAbsoluteName() =
        "ruff.$this"
    
    /**
     * Return the information about the configuration,
     * rendered as a documentation popup.
     * 
     * @see render
     */
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        element.project.getDocumentation(option)?.toDocumentationResult()
    }
    
    private suspend fun Project.getDocumentation(option: OptionName) =
        when (ruffConfigurations.cacheConfigDocumentation) {
            true -> getDocumentationCached(option)
            else -> getDocumentationUncached(option)
        }
    
    private suspend fun Project.getDocumentationUncached(option: OptionName): OptionDocumentation? {
        val ruff = this.ruff ?: return null
        
        val command = ruff.config(option)
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
            val info = output.stdout.parseAsJSONLeniently<OptionInfo>()
            
            info?.render(option.toAbsoluteName())
        }
    }
    
    private suspend fun Project.getDocumentationCached(option: OptionName): OptionDocumentation? {
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
        
        val newData = getNewDocumentationDataForCache()?.also {
            cache.optionsDocumentation = CachedResult(it, executable)
        }
        
        return newData?.get(option)
    }
    
    private suspend fun Project.getNewDocumentationDataForCache(): Map<OptionName, OptionDocumentation>? {
        val command = ruff!!.allConfig()
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
            val map = output.stdout.parseAsJSONLeniently<Map<OptionName, OptionInfo>>()
            
            map?.mapValues { (name, info) ->
                info.render(name.toAbsoluteName())
            }
        }
    }
    
}
