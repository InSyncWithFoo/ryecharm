package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.OptionName
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelector
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


internal fun Ruff.ruleInfo(code: RuleCode) =
    rule(CommandArguments(code))


internal fun Ruff.allRulesInfo(): Command {
    val arguments = CommandArguments("--all")
    
    arguments["--output-format"] = "json"
    
    return rule(arguments)
}


internal fun Ruff.configInfo(option: OptionName): Command {
    val arguments = CommandArguments("--output-format" to "json")
    
    if (option.isNotEmpty()) {
        arguments += option
    }
    
    return config(arguments)
}


internal fun Ruff.allConfigInfo() =
    config(CommandArguments("--output-format" to "json"))


internal fun Ruff.allLintersInfo() =
    linter(CommandArguments("--output-format" to "json"))


internal fun Ruff.optimizeImportsInStdinFile(text: String, path: Path?): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    arguments["--select"] = "I,F401"
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    return optimizeImports(arguments, text)
}


internal fun Ruff.fixStdinFile(text: String, path: Path?, select: List<RuleSelector>?, unsafeFixes: Boolean): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    if (select != null) {
        arguments["--select"] = select.joinToString(",")
    }
    
    if (unsafeFixes) {
        arguments += "--unsafe-fixes"
    }
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    return fix(arguments, text)
}


internal fun Ruff.fixAllInStdinFile(text: String, path: Path?, unsafeFixes: Boolean) =
    fixStdinFile(text, path, select = null, unsafeFixes)


internal fun Ruff.organizeImports(text: String, stdinFilename: Path?): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    arguments["--select"] = "I"
    
    if (stdinFilename != null) {
        arguments["--stdin-filename"] = stdinFilename.toString()
    }
    
    return organizeImports(arguments, text)
}


internal fun Ruff.showSettings(
    select: List<RuleSelector>? = null,
    isolated: Boolean = true,
    preview: Boolean = true
): Command {
    val arguments = CommandArguments("--show-settings")
    
    if (isolated) {
        arguments += "--isolated"
    }
    
    if (preview) {
        arguments += "--preview"
    }
    
    if (select != null) {
        arguments["--select"] = select.joinToString(",")
    }
    
    return showSettings(arguments)
}
