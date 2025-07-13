package insyncwithfoo.ryecharm.ruff.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.CommandArguments
import insyncwithfoo.ryecharm.ruff.OneBasedRange
import insyncwithfoo.ryecharm.ruff.RuleCode
import insyncwithfoo.ryecharm.ruff.documentation.OptionName
import insyncwithfoo.ryecharm.ruff.documentation.RuleSelector
import java.nio.file.Path


internal fun Ruff.check(text: String, path: Path?, considerAllFixable: Boolean): Command {
    val arguments = CommandArguments("--no-fix", "--exit-zero", "--quiet", "-")
    
    arguments["--output-format"] = "json"
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    if (considerAllFixable) {
        arguments["--fixable"] = "ALL"
    }
    
    return check(arguments, text)
}


internal fun Ruff.checkProject(considerAllFixable: Boolean): Command {
    val arguments = CommandArguments("--no-fix", "--exit-zero", "--quiet")
    
    arguments["--output-format"] = "json"
    
    if (considerAllFixable) {
        arguments["--fixable"] = "ALL"
    }
    
    return check(arguments)
}


internal fun Ruff.fix(text: String, path: Path?, rules: List<RuleSelector>?, unsafe: Boolean): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    if (rules != null) {
        arguments["--select"] = rules.joinToString(",")
    }
    
    if (unsafe) {
        arguments += "--unsafe-fixes"
    }
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    return fix(arguments, text)
}


internal fun Ruff.optimizeImports(text: String, path: Path?): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    arguments["--select"] = "I,F401"
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    return optimizeImports(arguments, text)
}


internal fun Ruff.organizeImports(text: String, path: Path?): Command {
    val arguments = CommandArguments("--fix", "--fix-only", "--exit-zero", "--quiet", "-")
    
    arguments["--select"] = "I"
    
    if (path != null) {
        arguments["--stdin-filename"] = path.toString()
    }
    
    return organizeImports(arguments, text)
}


internal fun Ruff.fixAll(text: String, path: Path?, unsafe: Boolean) =
    fix(text, path, rules = null, unsafe)


internal fun Ruff.showSettings(selectors: List<RuleSelector>): Command {
    val arguments = CommandArguments("--show-settings")
    
    arguments["--select"] = selectors.joinToString(",")
    
    return showSettings(arguments)
}


internal fun Ruff.format(text: String, path: Path?, range: OneBasedRange? = null, quiet: Boolean = true): Command {
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


internal fun Ruff.optionInfo(option: OptionName): Command {
    val arguments = CommandArguments("--output-format" to "json")
    
    if (option.isNotEmpty()) {
        arguments += option
    }
    
    return config(arguments)
}


internal fun Ruff.allOptionsInfo() =
    config(CommandArguments("--output-format" to "json"))


internal fun Ruff.allLintersInfo() =
    linter(CommandArguments("--output-format" to "json"))
