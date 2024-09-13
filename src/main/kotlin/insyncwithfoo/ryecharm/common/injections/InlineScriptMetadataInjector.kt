package insyncwithfoo.ryecharm.common.injections

import com.intellij.formatting.InjectedFormattingOptionsProvider
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiWhiteSpace
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import org.toml.lang.TomlLanguage


private val scriptBlock = """(?mx)
    ^\#\x20///\x20script\n
    (?<content>
        \#(?:\x20.*)?
        (?:\n\#(?:\x20.*)?)*
    )
    \n
    \#\x20///${'$'}
""".toRegex()


private val PyFile.topLevelComments: Sequence<PsiComment>
    get() = children.asSequence().filterIsInstance<PsiComment>()


private val PsiElement.isStartBlockLine: Boolean
    get() = this is PsiComment && textMatches("# /// script")


private val PsiElement.isEmptyBlockLine: Boolean
    get() = this is PsiComment && textMatches("#")


private val PsiElement.isBlockLine: Boolean
    get() = this is PsiComment && (this.isEmptyBlockLine || text.startsWith("# "))


private val PsiElement.isLineBreak: Boolean
    get() = this is PsiWhiteSpace && textMatches("\n")


private fun PyFile.findCommentAt(offset: Int) =
    findElementAt(offset) as? PsiComment


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
internal class InlineScriptMetadataInjector : MultiHostInjector, InjectedFormattingOptionsProvider {
    
    override fun elementsToInjectIn() = listOf(PsiComment::class.java)
    
    override fun shouldDelegateToTopLevel(file: PsiFile) = false
    
    /**
     * Register the ranges belong to a script metadata block.
     * 
     * To prevent injecting errors:
     * * 
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
        
        val scriptBlockComments = containingFile!!.findScriptBlock()
        
        if (comment !in scriptBlockComments) {
            return
        }
        
        registrar.registerInjectionRanges(scriptBlockComments)
    }
    
    /**
     * Search for the first block matching [scriptBlock],
     * then return a [Sequence] of [PsiComment]s constructing its content,
     * (that is, without the starting `# /// script` and the ending `# ///`).
     * 
     * If such a block is not found, return an empty sequence.
     */
    private fun PyFile.findScriptBlock(): Sequence<PsiComment> {
        val document = viewProvider.document ?: return emptySequence()
        val match = scriptBlock.find(document.charsSequence) ?: return emptySequence()
        
        val contentGroupRange = match.groups["content"]!!.range
        val (contentStart, contentEnd) = Pair(contentGroupRange.first, contentGroupRange.last)
        
        val firstComment = findCommentAt(contentStart) ?: return emptySequence()
        val lastComment = findCommentAt(contentEnd) ?: return emptySequence()
        
        val firstUntilLast = topLevelComments
            .dropWhile { it != firstComment }
            .takeWhile { it != lastComment }
        
        return firstUntilLast + sequenceOf(lastComment)
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
