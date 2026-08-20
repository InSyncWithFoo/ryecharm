package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.platform.eel.EelOsFamily
import com.intellij.platform.eel.provider.getEelDescriptor
import java.nio.file.Path


@Suppress("UnstableApiUsage")
internal val Project.osIsWindows: Boolean
    get() = getEelDescriptor().osFamily == EelOsFamily.Windows


@Suppress("UnstableApiUsage")
internal val Path.osIsWindows: Boolean
    get() = getEelDescriptor().osFamily == EelOsFamily.Windows
