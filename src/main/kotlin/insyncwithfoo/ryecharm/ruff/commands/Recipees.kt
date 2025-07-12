package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.RuleCode
import java.nio.file.Path


internal fun Ruff.checkStdinFile(text: String, path: Path?, allFixable: Boolean): Command {
    val arguments = CommandArguments("--no-fix", "--exit-zero", "--quiet", "-")
    
    arguments["--output-format"] = "json"
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    if (allFixable) {
        arguments["--fixable"] = "ALL"
    }
    
    return check(arguments, text)
}


internal fun Ruff.checkProject(allFixable: Boolean): Command {
    val arguments = CommandArguments("--no-fix", "--exit-zero", "--quiet")
    
    arguments["--output-format"] = "json"
    
    if (allFixable) {
        arguments["--fixable"] = "ALL"
    }
    
    return check(arguments)
}

internal fun Ruff.formatStdinFile(
    text: String,
    path: Path?,
    range: OneBasedRange? = null,
    quiet: Boolean = true
): Command {
    val arguments = CommandArguments("-")
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    if (range != null) {
        arguments["--range"] = range.toString()
    }
    
    if (quiet) {
        arguments += "--quiet"
    }
    
    return format(arguments, text)
}


internal fun Ruff.clean(directory: Path) =
    clean().also { it.workingDirectory = directory }


internal fun Ruff.rule(code: RuleCode) =
    rule(CommandArguments(code))


internal fun Ruff.allRules(): Command {
    val arguments = CommandArguments("--all")
    
    arguments["--output-format"] = "json"
    
    return rule(arguments)
}
