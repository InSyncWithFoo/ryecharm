package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.RuffLocalService
import insyncwithfoo.ryecharm.configurations.ruff.RuffOverrideService
import insyncwithfoo.ryecharm.configurations.ruff.globalRuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.ruffConfigurations
import insyncwithfoo.ryecharm.configurations.rye.globalRyeConfigurations
import insyncwithfoo.ryecharm.configurations.rye.ryeConfigurations
import insyncwithfoo.ryecharm.configurations.ty.TYConfigurations
import insyncwithfoo.ryecharm.configurations.ty.TYGlobalService
import insyncwithfoo.ryecharm.configurations.ty.TYLocalService
import insyncwithfoo.ryecharm.configurations.ty.TYOverrideService
import insyncwithfoo.ryecharm.configurations.ty.globalTYConfigurations
import insyncwithfoo.ryecharm.configurations.ty.tyConfigurations
import insyncwithfoo.ryecharm.configurations.uv.UVConfigurations
import insyncwithfoo.ryecharm.configurations.uv.UVGlobalService
import insyncwithfoo.ryecharm.configurations.uv.UVLocalService
import insyncwithfoo.ryecharm.configurations.uv.UVOverrideService
import insyncwithfoo.ryecharm.configurations.uv.globalUVConfigurations
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.findChildIgnoringExtension
import insyncwithfoo.ryecharm.findExecutableInVenv
import insyncwithfoo.ryecharm.interpreterDirectory
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.detectExecutable
import insyncwithfoo.ryecharm.rye.commands.Rye
import insyncwithfoo.ryecharm.rye.commands.detectExecutable
import insyncwithfoo.ryecharm.toNullIfNotExists
import insyncwithfoo.ryecharm.toPathIfItExists
import insyncwithfoo.ryecharm.toPathOrNull
import insyncwithfoo.ryecharm.ty.commands.TY
import insyncwithfoo.ryecharm.ty.commands.detectExecutable
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.detectExecutable
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension


/**
 * The Rye executable associated with this project, if it exists.
 */
internal val Project.ryeExecutable: Path?
    get() = ryeConfigurations.executable?.toPathIfItExists() ?: Rye.detectExecutable()


/**
 * The Ruff executable associated with this project,
 * as it is specified in the settings.
 */
private val Project.specifiedRuffExecutable: Path?
    get() {
        val configurations = ruffConfigurations
        val executable = configurations.executable?.toPathOrNull() ?: return null
        
        if (executable.isAbsolute) {
            return executable
        }
        
        if (!configurations.crossPlatformExecutableResolution) {
            return this.path?.resolve(executable)
        }
        
        val resolutionBase = when (val parent = executable.parent) {
            null -> interpreterDirectory
            else -> interpreterDirectory?.resolve(parent)
        }
        
        return resolutionBase?.findChildIgnoringExtension(executable.nameWithoutExtension)
    }


/**
 * The Ruff executable associated with this project, if it exists.
 */
internal val Project.ruffExecutable: Path?
    get() = specifiedRuffExecutable?.toNullIfNotExists()
        ?: Ruff.detectExecutable()
        ?: findExecutableInVenv("ruff")


/**
 * The uv executable associated with this project, if it exists.
 */
internal val Project.uvExecutable: Path?
    get() = uvConfigurations.executable?.toPathIfItExists() ?: UV.detectExecutable()


/**
 * The ty executable associated with this project, if it exists.
 */
internal val Project.tyExecutable: Path?
    get() = tyConfigurations.executable?.toPathIfItExists() ?: TY.detectExecutable()


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
 * The ty executable defined in the project panel,
 * or one detected in PATH.
 */
internal val globalTYExecutable: Path?
    get() = globalTYConfigurations.executable?.toPathIfItExists() ?: TY.detectExecutable()


/**
 * Change global uv configurations in-place.
 */
internal fun changeGlobalUVConfigurations(action: UVConfigurations.() -> Unit) {
    UVGlobalService.getInstance().state.apply(action)
}


/**
 * Change global ty configurations in-place.
 */
internal fun changeGlobalTYConfigurations(action: TYConfigurations.() -> Unit) {
    TYGlobalService.getInstance().state.apply(action)
}


/**
 * Change Ruff configurations in-place.
 * 
 * @see changeRuffOverrides
 */
internal fun Project.changeRuffConfigurations(action: RuffConfigurations.() -> Unit) {
    RuffLocalService.getInstance(this).state.apply(action)
}


/**
 * Change Ruff overrides in-place.
 * 
 * @see changeRuffConfigurations
 * @see add
 */
internal fun Project.changeRuffOverrides(action: Overrides.() -> Unit) {
    RuffOverrideService.getInstance(this).state.names.apply(action)
}


/**
 * Change uv configurations in-place.
 * 
 * @see changeUVOverrides
 */
internal fun Project.changeUVConfigurations(action: UVConfigurations.() -> Unit) {
    UVLocalService.getInstance(this).state.apply(action)
}


/**
 * Change uv overrides in-place.
 * 
 * @see changeUVConfigurations
 * @see add
 */
internal fun Project.changeUVOverrides(action: Overrides.() -> Unit) {
    UVOverrideService.getInstance(this).state.names.apply(action)
}


/**
 * Change ty configurations in-place.
 *
 * @see changeTYOverrides
 */
internal fun Project.changeTYConfigurations(action: TYConfigurations.() -> Unit) {
    TYLocalService.getInstance(this).state.apply(action)
}


/**
 * Change ty overrides in-place.
 * 
 * @see changeTYConfigurations
 * @see add
 */
internal fun Project.changeTYOverrides(action: Overrides.() -> Unit) {
    TYOverrideService.getInstance(this).state.names.apply(action)
}
