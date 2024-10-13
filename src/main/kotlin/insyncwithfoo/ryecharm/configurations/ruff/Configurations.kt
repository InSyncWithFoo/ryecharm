package insyncwithfoo.ryecharm.configurations.ruff

import insyncwithfoo.ryecharm.Commented
import insyncwithfoo.ryecharm.Keyed
import insyncwithfoo.ryecharm.Labeled
import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal enum class RunningMode(override val label: String) : Labeled {
    COMMAND_LINE(message("configurations.ruff.runningMode.commandLine")),
    LSP4IJ(message("configurations.ruff.runningMode.lsp4ij")),
    LSP(message("configurations.ruff.runningMode.lsp"));
}


@Suppress("unused")
internal enum class TooltipFormat(private val key: String) : Labeled {
    MESSAGE("message"),
    RULE_MESSAGE("ruleMessage"),
    RUFF_RULE_MESSAGE("ruffRuleMessage"),
    MESSAGE_RUFF_RULE("messageRuffRule");
    
    operator fun rem(messageAndRule: Pair<String, String?>): String {
        val (message, rule) = messageAndRule
        
        return when {
            rule != null -> message("configurations.ruff.tooltipFormat.$key.withRule", rule, message)
            else -> message("configurations.ruff.tooltipFormat.$key.withoutRule", message)
        }
    }
    
    override val label: String
        get() = this % Pair(message("configurations.ruff.tooltipFormat.placeholder"), "A123")
}


@Suppress("unused")
internal enum class LogLevel(override val label: String) : Labeled {
    TRACE(message("configurations.ruff.logLevel.trace")),
    DEBUG(message("configurations.ruff.logLevel.debug")),
    INFO(message("configurations.ruff.logLevel.info")),
    WARN(message("configurations.ruff.logLevel.warn")),
    ERROR(message("configurations.ruff.logLevel.error"));
    
    override fun toString() = name.lowercase()
}


internal class RuffConfigurations : DisplayableState(), HasTimeouts, Copyable {
    var executable by string(null)
    var crossPlatformExecutableResolution by property(true)
    var configurationFile by string(null)
    
    var runningMode by enum(RunningMode.COMMAND_LINE)
    
    var linting by property(true)
    var showSyntaxErrors by property(false)
    var tooltipFormat by enum(TooltipFormat.RULE_MESSAGE)
    
    var quickFixes by property(true)
    var fixAll by property(true)
    var organizeImports by property(true)
    var disableRuleComment by property(true)
    var fixViolation by property(true)
    
    var formatting by property(true)
    var formatOnReformat by property(true)
    var formatOnOptimizeImports by property(true)
    
    var documentationPopups by property(true)
    var documentationPopupsForNoqaComments by property(true)
    var documentationPopupsForTOMLRuleCodes by property(true)
    var documentationPopupsForTOMLOptions by property(true)
    
    var logLevel by enum(LogLevel.INFO)
    var logFile by string(null)
    
    var runOnSaveProjectFilesOnly by property(true)
    var formatOnSave by property(false)
    var fixOnSave by property(false)
    
    var suggestExecutableOnProjectOpen by property(true)
    var suggestExecutableOnPackagesChange by property(true)
    
    var autoRestartServers by property(true)
    
    override var timeouts by map<SettingName, MillisecondsOrNoLimit>()
}


internal enum class RuffTimeouts(override val key: String, override val comment: String) : Keyed, Commented {
    CHECK("check", message("configurations.timeouts.ruff.check")),
    FORMAT("format", message("configurations.timeouts.ruff.format")),
    CLEAN("clean", message("configurations.timeouts.ruff.clean")),
    RULE("rule", message("configurations.timeouts.ruff.rule")),
    CONFIG("config", message("configurations.timeouts.ruff.config")),
    LINTER("linter", message("configurations.timeouts.ruff.linter")),
    VERSION("version", message("configurations.timeouts.ruff.version"));
    
    override val label by ::key
}


internal class RuffOverrides : DisplayableState(), ProjectOverrideState {
    override var names by map<SettingName, Boolean>()
}
