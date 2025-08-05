package insyncwithfoo.ryecharm.uv.commands

import com.jetbrains.python.packaging.common.PythonRepositoryPackageSpecification
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


internal fun UV.add(target: PythonRepositoryPackageSpecification) =
    add(CommandArguments(target.nameWithVersionSpec))


internal fun UV.upgrade(target: PythonRepositoryPackageSpecification) =
    upgrade(CommandArguments(target.nameWithVersionSpec, "--upgrade"))


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


internal fun UV.getProjectVersion() =
    version(CommandArguments("--short"))


internal fun UV.bumpProjectVersion(bumpType: VersionBumpType): Command {
    val arguments = CommandArguments("--short")
    
    arguments["--bump"] = bumpType.toString()
    
    return version(arguments)
}


internal fun UV.setProjectVersion(newVersion: ProjectVersion) =
    version(CommandArguments("--short", newVersion))


internal fun UV.venv(baseInterpreter: Path, name: String? = null): Command {
    val arguments = CommandArguments("--python" to baseInterpreter.toString())
    
    if (name != null) {
        arguments += name
    }
    
    return venv(arguments)
}


internal fun UV.selfVersion(json: Boolean = false): Command {
    val arguments = CommandArguments()
    
    if (json) {
        arguments["--output-format"] = "json"
    }
    
    return selfVersion(arguments)
}


internal fun UV.pipCompile(packages: List<String>, noHeader: Boolean = true): Command {
    val arguments = CommandArguments("-")
    val stdin = packages.joinToString("\n")
    
    if (noHeader) {
        arguments += "--no-header"
    }
    
    return pipCompile(arguments, stdin)
}


internal fun UV.pipList(interpreter: Path? = null): Command {
    val arguments = CommandArguments("--quiet")
    
    arguments["--format"] = "json"
    
    if (interpreter != null) {
        arguments["--python"] = interpreter.toString()
    }
    
    return pipList(arguments)
}


// TODO: `--prune`, `--strict` (?)
internal fun UV.pipTree(
    `package`: String,
    inverted: Boolean,
    showVersionSpecifiers: Boolean,
    showLatestVersions: Boolean,
    dedupe: Boolean,
    depth: Int,
    interpreter: Path?
): Command {
    val arguments = CommandArguments("--package" to `package`, "--depth" to depth.toString())
    
    if (inverted) {
        arguments += "--invert"
    }
    
    if (showVersionSpecifiers) {
        arguments += "--show-version-specifiers"
    }
    
    if (showLatestVersions) {
        arguments += "--outdated"
    }
    
    if (!dedupe) {
        arguments += "--no-dedupe"
    }
    
    if (interpreter != null) {
        arguments["--python"] = interpreter.toString()
    }
    
    return pipTree(arguments)
}
