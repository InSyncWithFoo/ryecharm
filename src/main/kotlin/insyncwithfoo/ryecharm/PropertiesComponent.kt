package insyncwithfoo.ryecharm

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project


internal val propertiesComponent: PropertiesComponent
    get() = PropertiesComponent.getInstance()


internal val Project.propertiesComponent: PropertiesComponent
    get() = PropertiesComponent.getInstance(this)
