package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.redknot.globalRedKnotConfigurations
import insyncwithfoo.ryecharm.configurations.redknot.redKnotConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.RuffLocalService
import insyncwithfoo.ryecharm.configurations.ruff.RuffOverrideService
import insyncwithfoo.ryecharm.configurations.ruff.globalRuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.rye.globalRyeConfigurations
import insyncwithfoo.ryecharm.configurations.rye.ryeConfigurations
import insyncwithfoo.ryecharm.configurations.uv.globalUVConfigurations
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.findExecutableInVenv
import insyncwithfoo.ryecharm.interpreterDirectory
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.redknot.commands.RedKnot
import insyncwithfoo.ryecharm.redknot.commands.detectExecutable
import insyncwithfoo.ryecharm.removeExtension
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.detectExecutable
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.detectExecutable
import insyncwithfoo.ryecharm.toNullIfNotExists
import insyncwithfoo.ryecharm.toPathIfItExists
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.detectExecutable
import java.nio.file.Path


/**
 * The Rye executable associated with this project, if it exists.
 */
internal val Project.ryeExecutable: Path?
    get() = ryeConfigurations.executable?.toPathIfItExists() ?: Rye.detectExecutable()


/**
 * The Ruff executable associated with this project, if it exists.
 */
internal val Project.ruffExecutable: Path?
    get() {
        val configurations = ruffConfigurations
        val executable = configurations.executable?.toPathOrNull()
        
        if (executable?.isAbsolute == true) {
            return executable.toNullIfNotExists()
        }
        
        val resolutionBase = when {
            configurations.crossPlatformExecutableResolution -> interpreterDirectory
            else -> this.path
        }
        val resolved = executable?.removeExtension()?.let { resolutionBase?.resolve(it) }
        
        return resolved?.toNullIfNotExists()
            ?: Ruff.detectExecutable()
            ?: findExecutableInVenv("ruff")
    }


/**
 * The uv executable associated with this project, if it exists.
 */
internal val Project.uvExecutable: Path?
    get() = uvConfigurations.executable?.toPathIfItExists() ?: UV.detectExecutable()


/**
 * The Red Knot executable associated with this project, if it exists.
 */
internal val Project.redKnotExecutable: Path?
    get() = redKnotConfigurations.executable?.toPathIfItExists() ?: RedKnot.detectExecutable()


/**
 * The Rye executable defined in the global panel,
 * or one detected in PATH.
 */
internal val globalRyeExecutable: Path?
    get() = globalRyeConfigurations.executable?.toPathIfItExists() ?: Rye.detectExecutable()


/**
 * The Ruff executable defined in the project panel,
 * or one detected in PATH.
 */
internal val globalRuffExecutable: Path?
    get() = globalRuffConfigurations.executable?.toPathIfItExists() ?: Ruff.detectExecutable()


/**
 * The uv executable defined in the project panel,
 * or one detected in PATH.
 */
internal val globalUVExecutable: Path?
    get() = globalUVConfigurations.executable?.toPathIfItExists() ?: UV.detectExecutable()

/**
 * The Red Knot executable defined in the project panel,
 * or one detected in PATH.
 */
internal val globalRedKnotExecutable: Path?
    get() = globalRedKnotConfigurations.executable?.toPathIfItExists() ?: RedKnot.detectExecutable()


/**
 * Change Ruff configurations in-place.
 */
internal fun Project.changeRuffConfigurations(action: RuffConfigurations.() -> Unit) {
    RuffLocalService.getInstance(this).state.apply(action)
}


internal fun Project.changeRuffOverrides(action: Overrides.() -> Unit) {
    RuffOverrideService.getInstance(this).state.names.apply(action)
}
