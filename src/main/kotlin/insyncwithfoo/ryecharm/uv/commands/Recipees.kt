package insyncwithfoo.ryecharm.uv.commands

import com.jetbrains.python.packaging.common.PythonPackageSpecification
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.message
import java.nio.file.Path


internal fun UV.init(
    name: String?,
    kind: ProjectKind,
    createReadme: Boolean,
    pinPython: Boolean,
    baseInterpreter: Path
): Command {
    val arguments = CommandArguments("--no-workspace")
    
    arguments["--python"] = baseInterpreter.toString()
    
    if (name != null) {
        arguments["--name"] = name
    }
    
    when (kind) {
        ProjectKind.APP -> arguments += "--app"
        ProjectKind.LIBRARY -> arguments += "--lib"
        ProjectKind.PACKAGED_APP -> arguments += listOf("--app", "--package")
    }
    
    if (!createReadme) {
        arguments += "--no-readme"
    }
    
    if (!pinPython) {
        arguments += "--no-pin-python"
    }
    
    return init(arguments)
}


internal fun UV.add(target: PythonPackageSpecification) =
    add(CommandArguments(target.toPEP508Format()))


internal fun UV.upgrade(target: PythonPackageSpecification) =
    upgrade(CommandArguments(target.toPEP508Format(), "--upgrade"))


internal fun UV.remove(target: String) =
    remove(CommandArguments(target))


internal fun UV.installGroup(name: String): Command {
    val kind = message("progresses.command.uv.installDependencies.kind.group", name)
    
    return installDependencies(kind, CommandArguments("--group" to name))
}


internal fun UV.installAllGroups(): Command {
    val kind = message("progresses.command.uv.installDependencies.kind.allGroups")
    
    return installDependencies(kind, CommandArguments("--all-groups"))
}


internal fun UV.installExtra(name: String): Command {
    val kind = message("progresses.command.uv.installDependencies.kind.extra", name)
    
    return installDependencies(kind, CommandArguments("--extra" to name))
}


internal fun UV.installAllExtras(): Command {
    val kind = message("progresses.command.uv.installDependencies.kind.allExtras")
    
    return installDependencies(kind, CommandArguments("--all-extras"))
}


internal fun UV.version() =
    version(CommandArguments("--short"))


internal fun UV.version(bumpType: VersionBumpType): Command {
    val arguments = CommandArguments("--short")
    
    arguments["--bump"] = bumpType.toString()
    
    return version(arguments)
}


internal fun UV.version(newVersion: ProjectVersion) =
    version(CommandArguments("--short", newVersion))


internal fun UV.selfVersion(json: Boolean = false): Command {
    val arguments = CommandArguments()
    
    if (json) {
        arguments["--output-format"] = "json"
    }
    
    return selfVersion(arguments)
}
