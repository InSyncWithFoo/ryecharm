package insyncwithfoo.ryecharm.ty

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Builder
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.invoke


internal data class Completions(
    var enable: Boolean = false
) : Builder


internal data class Experimental(
    val completions: Completions = Completions()
) : Builder


internal data class InitializationOptions(
    val experimental: Experimental = Experimental()
)


internal fun Project.createInitializationOptionsObject() = InitializationOptions().apply {
    val configurations = tyConfigurations
    
    experimental {
        completions { enable = configurations.completion }
    }
}
