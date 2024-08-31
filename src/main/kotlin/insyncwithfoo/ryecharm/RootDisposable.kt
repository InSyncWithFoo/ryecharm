package insyncwithfoo.ryecharm

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project


@Service(Service.Level.APP, Service.Level.PROJECT)
internal class RootDisposable : Disposable {
    
    override fun dispose() {}
    
    companion object {
        
        fun getInstance() = service<RootDisposable>()
        
        fun getInstance(project: Project) = project.service<RootDisposable>()
        
    }
}
