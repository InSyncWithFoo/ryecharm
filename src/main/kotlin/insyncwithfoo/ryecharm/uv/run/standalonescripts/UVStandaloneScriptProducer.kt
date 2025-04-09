package insyncwithfoo.ryecharm.uv.run.standalonescripts

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.others.scriptmetadata.scriptBlock
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationFactory
import insyncwithfoo.ryecharm.uv.run.UVRunConfigurationProducer
import kotlin.io.path.name
import kotlin.reflect.KClass


private val PsiElement.isInsideScriptMetadataBlock: Boolean
    get() {
        if (this !is PsiComment && this !is PsiWhiteSpace) {
            return false
        }
        
        val file = containingFile as? PyFile ?: return false
        
        if (parent != file) {
            return false
        }
        
        val document = file.viewProvider.document ?: return false
        val blockRange = scriptBlock.find(document.charsSequence)?.range ?: return false
        
        return blockRange.first <= startOffset && endOffset <= blockRange.last + 1
    }


/**
 * Show a "Run" action that runs the current script
 * when its metadata block is right-clicked.
 */
internal class UVStandaloneScriptProducer : UVRunConfigurationProducer<UVStandaloneScript>(), DumbAware {
    
    override val runConfigurationClass: KClass<UVStandaloneScript>
        get() = UVStandaloneScript::class
    
    override fun getConfigurationFactory() =
        UVRunConfigurationFactory.instances[3] as UVStandaloneScriptFactory
    
    override fun isConfigurationFromContext(configuration: UVStandaloneScript, context: ConfigurationContext): Boolean {
        val element = context.location?.psiElement ?: return false
        
        if (!element.isInsideScriptMetadataBlock) {
            return false
        }
        
        val file = element.containingFile ?: return false
        val path = file.virtualFile?.toNioPathOrNull() ?: return false
        
        return configuration.settings.scriptPath == path.toString()
    }
    
    override fun setupConfigurationFromContext(
        configuration: UVStandaloneScript,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val element = sourceElement.get() ?: return false
        
        if (!element.isInsideScriptMetadataBlock) {
            return false
        }
        
        val file = element.containingFile ?: return false
        val path = file.virtualFile?.toNioPathOrNull() ?: return false
        
        configuration.apply {
            name = message("runConfigurations.instance.standaloneScript.nameTemplate", path.name)
            settings.scriptPath = path.toString()
        }
        
        return true
    }
    
}
