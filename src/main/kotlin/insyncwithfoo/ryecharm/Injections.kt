package insyncwithfoo.ryecharm

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile


private operator fun <A, B> com.intellij.openapi.util.Pair<A, B>.component1() = first
private operator fun <A, B> com.intellij.openapi.util.Pair<A, B>.component2() = second


internal val Project.injectedLanguageManager: InjectedLanguageManager
    get() = InjectedLanguageManager.getInstance(this)


internal val PsiElement.hostFile: PsiFile
    get() = project.injectedLanguageManager.getTopLevelFile(this)


internal val PsiElement.host: PsiElement?
    get() = project.injectedLanguageManager.getInjectionHost(this)


internal val PsiElement.injectedFiles: List<PsiElement>
    get() = project.injectedLanguageManager.getInjectedPsiFiles(this)?.map { (element, _) -> element }
        ?: emptyList()
