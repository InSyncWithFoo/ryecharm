package insyncwithfoo.ryecharm.others.consolefilters

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.main.mainConfigurations
import insyncwithfoo.ryecharm.path


private inline fun <reified T : Any> arrayOfNotNull(vararg elements: T?) =
    listOfNotNull(*elements).toTypedArray()


internal class RyeCharmConsoleFilterProvider : ConsoleFilterProvider {
    
    override fun getDefaultFilters(project: Project): Array<out Filter> {
        val configurations = project.mainConfigurations
        
        return arrayOfNotNull(
            TYPathLinker(project).takeIf { configurations.consoleFilterTYPaths && project.path != null }
        )
    }
    
}
