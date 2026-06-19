package insyncwithfoo.ryecharm

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project


@Service(Service.Level.APP, Service.Level.PROJECT)
internal class RootDisposable(private val project: Project? = null) : Disposable {
    
    init {
        thisLogger().info("RootDisposable initialized; $project; ${this::class.loaderID}")
        thisLogger().info(Throwable().stackTraceToString())
    }
    
    override fun dispose() {
        thisLogger().info("RootDisposable disposed; $project; ${this::class.loaderID}")
        thisLogger().info(Throwable().stackTraceToString())
    }
    
    companion object {
        fun getInstance() = service<RootDisposable>()
        fun getInstance(project: Project) = project.service<RootDisposable>()
    }
    
}
