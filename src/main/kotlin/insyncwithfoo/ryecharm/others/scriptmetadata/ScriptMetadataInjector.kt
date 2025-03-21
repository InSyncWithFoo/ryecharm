package insyncwithfoo.ryecharm.others.scriptmetadata

import com.intellij.formatting.InjectedFormattingOptionsProvider
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLanguageInjectionHost
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import org.toml.lang.TomlLanguage


private fun MultiHostRegistrar.inject(language: Language, addRanges: MultiHostRegistrar.() -> Unit) {
    startInjecting(language, language.associatedFileType?.defaultExtension)
    addRanges()
    doneInjecting()
}


private fun MultiHostRegistrar.addPlace(comment: PsiComment, range: TextRange) {
    val (prefix, suffix) = Pair("", "\n")
    val host = comment as PsiLanguageInjectionHost
    
    addPlace(prefix, suffix, host, range)
}


/**
 * Inject TOML fragments for PEP 723
 * inline script metadata blocks.
 */
internal class ScriptMetadataInjector : MultiHostInjector, InjectedFormattingOptionsProvider, DumbAware {
    
    override fun elementsToInjectIn() = listOf(PsiComment::class.java)
    
    override fun shouldDelegateToTopLevel(file: PsiFile) = false
    
    /**
     * Register the ranges belong to a script metadata block.
     * 
     * [context] is verified to be the first comment after
     * a top-level `# /// script` comment.
     */
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val configurations = context.project.mainConfigurations
        
        val comment = context as PsiComment
        val previousLineBreak = comment.prevSibling?.takeIf { it.isLineBreak }
        
        val containingFile = comment.containingFile as? PyFile
        val virtualFile = containingFile?.virtualFile
        val fileExtension = virtualFile?.extension
        
        when {
            !configurations.languageInjectionPEP723Blocks -> return
            !comment.isBlockLine -> return
            containingFile == null || comment.parent != containingFile -> return
            fileExtension != "py" && fileExtension != null -> return
            previousLineBreak?.prevSibling?.isStartBlockLine != true -> return
        }
        
        val scriptBlockComments = containingFile!!.findScriptBlockBodyElements()
        
        if (comment !in scriptBlockComments) {
            return
        }
        
        registrar.registerInjectionRanges(scriptBlockComments)
    }
    
    private fun MultiHostRegistrar.registerInjectionRanges(comments: Sequence<PsiComment>) = inject(TomlLanguage) {
        comments.forEach { comment ->
            val prefix = when {
                comment.isEmptyBlockLine -> "#"
                else -> "# "
            }
            val range = TextRange(prefix.length, comment.textLength)
            
            addPlace(comment, range)
        }
    }
    
}
