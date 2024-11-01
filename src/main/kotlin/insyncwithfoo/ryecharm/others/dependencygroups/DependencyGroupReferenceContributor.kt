package insyncwithfoo.ryecharm.others.dependencygroups

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isString
import insyncwithfoo.ryecharm.keyValuePair
import insyncwithfoo.ryecharm.pep508Normalize
import insyncwithfoo.ryecharm.stringContent
import org.toml.lang.psi.TomlArray
import org.toml.lang.psi.TomlInlineTable
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlLiteral


private class DependencyGroupReference(element: GroupNameString) : PsiReferenceBase<GroupNameString>(element) {
    
    override fun resolve(): TomlKeySegment? {
        val includeGroupTable = element.keyValuePair!!.parent as IncludeGroupTable
        val groupArray = includeGroupTable.parent as GroupArray
        val dependencyGroupsTable = groupArray.keyValuePair!!.parent as DependencyGroupsTable
        
        val groupKeys = dependencyGroupsTable.groupKeys
        val includedGroupName = element.stringContent!!.pep508Normalize()
        
        val key = groupKeys.find { it.groupName == includedGroupName }
        
        return key?.segments?.singleOrNull()
    }
    
}


internal class DependencyGroupReferenceProvider : PsiReferenceProvider() {
    
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element.containingFile.virtualFile?.isPyprojectToml != true) {
            return emptyArray()
        }
        
        val string = (element as? TomlLiteral)?.takeIf { it.isString } ?: return emptyArray()
        val propertyPair = string.keyValuePair?.takeIf { it.isIncludeGroup } ?: return emptyArray()
        
        val inlineTable = propertyPair.parent as? TomlInlineTable ?: return emptyArray()
        val array = inlineTable.parent as? TomlArray ?: return emptyArray()
        val arrayKey = array.keyValuePair?.key ?: return emptyArray()
        
        return when (arrayKey.absoluteName isChildOf "dependency-groups") {
            true -> arrayOf(DependencyGroupReference(string))
            else -> emptyArray()
        }
    }
    
}


internal class DependencyGroupReferenceContributor : PsiReferenceContributor() {
    
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val pattern = PlatformPatterns.psiElement(TomlLiteral::class.java)
        
        registrar.registerReferenceProvider(pattern, DependencyGroupReferenceProvider())
    }
    
}
