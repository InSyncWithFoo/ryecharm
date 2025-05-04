package insyncwithfoo.ryecharm.ty.lsp4ij

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.redhat.devtools.lsp4ij.AbstractDocumentMatcher
import insyncwithfoo.ryecharm.isSupportedByTY


internal class TYServerDocumentMatcher : AbstractDocumentMatcher() {
    
    override fun match(file: VirtualFile, project: Project): Boolean {
        return file.isSupportedByTY(project)
    }
    
}
