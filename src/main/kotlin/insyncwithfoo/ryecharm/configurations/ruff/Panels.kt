package insyncwithfoo.ryecharm.configurations.ruff

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import insyncwithfoo.ryecharm.bindItem
import insyncwithfoo.ryecharm.bindSelected
import insyncwithfoo.ryecharm.bindText
import insyncwithfoo.ryecharm.comboBox
import insyncwithfoo.ryecharm.configurations.AdaptivePanel
import insyncwithfoo.ryecharm.configurations.Overrides
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import insyncwithfoo.ryecharm.configurations.projectAndOverrides
import insyncwithfoo.ryecharm.emptyText
import insyncwithfoo.ryecharm.fallbackValue
import insyncwithfoo.ryecharm.findExecutableInVenv
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.radioButtonForPotentiallyUnavailable
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.detectExecutable
import insyncwithfoo.ryecharm.singleFileTextField


private class RuffPanel(state: RuffConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<RuffConfigurations>(state, overrides, project)


private fun Row.executableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.crossPlatformExecutableResolutionInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.crossPlatformExecutableResolution.label")).apply(block)


private fun Row.configurationFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Panel.runningModeInputGroup(block: Panel.() -> Unit) =
    buttonsGroup(init = block)


private fun Row.lintingInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.linting.label")).apply(block)


private fun Row.showSyntaxErrorsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.showSyntaxErrors.label")).apply(block)


private fun Row.fileLevelBannerInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fileLevelBanner.label")).apply(block)


private fun Row.renderTooltipsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.renderTooltips.label")).apply(block)


private fun Row.tooltipFormatInput(block: Cell<ComboBox<TooltipFormat>>.() -> Unit) =
    comboBox<TooltipFormat>().label(message("configurations.ruff.tooltipFormat.label")).apply(block)


private fun Row.quickFixesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.quickFixes.label")).apply(block)


private fun Row.fixAllInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixAll.label")).apply(block)


private fun Row.organizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.organizeImports.label")).apply(block)


private fun Row.disableRuleCommentInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.disableRuleComment.label")).apply(block)


private fun Row.fixViolationInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixViolation.label")).apply(block)


private fun Row.fixSimilarViolationsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixSimilarViolations.label")).apply(block)


private fun Row.considerAllFixableInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.considerAllFixable.label")).apply(block)


private fun Row.formattingInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatting.label")).apply(block)


private fun Row.formatOnReformatInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnReformat.label")).apply(block)


private fun Row.formatOnOptimizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnOptimizeImports.label")).apply(block)


private fun Row.documentationPopupsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopups.label")).apply(block)


private fun Row.documentationPopupsForNoqaCommentsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopupsForNoqaComments.label")).apply(block)


private fun Row.documentationPopupsForTOMLRuleCodesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopupsForTOMLRuleCodes.label")).apply(block)


private fun Row.documentationPopupsForTOMLOptionsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopupsForTOMLOptions.label")).apply(block)


private fun Row.logLevelInput(block: Cell<ComboBox<LogLevel>>.() -> Unit) =
    comboBox<LogLevel>().apply(block)


private fun Row.logFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.runOnSaveProjectFilesOnlyInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.runOnSaveProjectFilesOnly.label")).apply(block)


private fun Row.formatOnSaveInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnSave.label")).apply(block)


private fun Row.fixOnSaveInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixOnSave.label")).apply(block)


private fun Row.suggestExecutableOnProjectOpenInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnProjectOpen.label")).apply(block)


private fun Row.suggestExecutableOnPackagesChangeInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnPackagesChange.label")).apply(block)


private fun Row.foldSingleRuleSelectorsByDefaultInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.foldSingleRuleSelectorsByDefault.label")).apply(block)


private fun Row.foldNoqaCodesByDefaultInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.foldNoqaCodesByDefault.label")).apply(block)


private fun Row.autoRestartServersInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.autoRestartServers.label")).apply(block)


private fun Row.snoozeFormattingTaskErrorInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.snoozeFormattingTaskError.label")).apply(block)


private fun Row.letNativeClientPullDiagnosticsInput(block: Cell<JBCheckBox>.() -> Unit) = run {
    val comment = message("configurations.ruff.letNativeClientPullDiagnostics.comment")
    
    checkBox(message("configurations.ruff.letNativeClientPullDiagnostics.label")).comment(comment).apply(block)
}


@Suppress("DialogTitleCapitalization")
private fun RuffPanel.makeComponent() = panel {
    
    row(message("configurations.ruff.executable.label")) {
        executableInput {
            val detectedExecutable = Ruff.detectExecutable()?.toString()
                ?: project?.findExecutableInVenv("ruff")?.toString()
            
            bindText(state::executable)
            
            fallbackValue = detectedExecutable
            emptyText = detectedExecutable ?: message("configurations.ruff.executable.placeholder")
        }
        overrideCheckbox(state::executable)
    }
    row("") {
        crossPlatformExecutableResolutionInput { bindSelected(state::crossPlatformExecutableResolution) }
        overrideCheckbox(state::crossPlatformExecutableResolution)
    }
    
    row(message("configurations.ruff.configurationFile.label")) {
        configurationFileInput { bindText(state::configurationFile) }
        overrideCheckbox(state::configurationFile)
    }
    
    val runningModeInputGroup = runningModeInputGroup {
        row(message("configurations.ruff.runningMode.label")) {
            radioButtonFor(RunningMode.COMMAND_LINE)
            radioButtonForPotentiallyUnavailable(RunningMode.LSP4IJ) { lsp4ijIsAvailable }
            radioButtonForPotentiallyUnavailable(RunningMode.LSP) { lspIsAvailable }
            
            overrideCheckbox(state::runningMode)
        }
    }
    runningModeInputGroup.bindSelected(state::runningMode)
    
    group(message("configurations.ruff.groups.main")) {
        
        row {
            lintingInput { bindSelected(state::linting) }
            overrideCheckbox(state::linting)
        }
        indent {
            row {
                showSyntaxErrorsInput { bindSelected(state::showSyntaxErrors) }
                overrideCheckbox(state::showSyntaxErrors)
            }
            row {
                fileLevelBannerInput { bindSelected(state::fileLevelBanner) }
                overrideCheckbox(state::fileLevelBanner)
            }
            row {
                renderTooltipsInput { bindSelected(state::renderTooltips) }
                overrideCheckbox(state::renderTooltips)
            }
            row {
                tooltipFormatInput { bindItem(state::tooltipFormat) }
                overrideCheckbox(state::tooltipFormat)
            }
        }
        
        row {
            quickFixesInput { bindSelected(state::quickFixes) }
            overrideCheckbox(state::quickFixes)
        }
        indent {
            row {
                fixAllInput { bindSelected(state::fixAll) }
                overrideCheckbox(state::fixAll)
            }
            row {
                organizeImportsInput { bindSelected(state::organizeImports) }
                overrideCheckbox(state::organizeImports)
            }
            row {
                disableRuleCommentInput { bindSelected(state::disableRuleComment) }
                overrideCheckbox(state::disableRuleComment)
            }
            row {
                fixViolationInput { bindSelected(state::fixViolation) }
                overrideCheckbox(state::fixViolation)
            }
            row {
                fixSimilarViolationsInput { bindSelected(state::fixSimilarViolations) }
                overrideCheckbox(state::fixSimilarViolations)
            }
            
            separator()
            
            row {
                considerAllFixableInput { bindSelected(state::considerAllFixable) }
                overrideCheckbox(state::considerAllFixable)
            }
        }
        
        row {
            formattingInput { bindSelected(state::formatting) }
            overrideCheckbox(state::formatting)
        }
        indent {
            row {
                formatOnReformatInput { bindSelected(state::formatOnReformat) }
                overrideCheckbox(state::formatOnReformat)
            }
            row {
                formatOnOptimizeImportsInput { bindSelected(state::formatOnOptimizeImports) }
                overrideCheckbox(state::formatOnOptimizeImports)
            }
        }
        
        row {
            documentationPopupsInput { bindSelected(state::documentationPopups) }
            overrideCheckbox(state::documentationPopups)
        }
        indent {
            row {
                documentationPopupsForNoqaCommentsInput { bindSelected(state::documentationPopupsForNoqaComments) }
                overrideCheckbox(state::documentationPopupsForNoqaComments)
            }
            row {
                documentationPopupsForTOMLRuleCodesInput { bindSelected(state::documentationPopupsForTOMLRuleCodes) }
                overrideCheckbox(state::documentationPopupsForTOMLRuleCodes)
            }
            row {
                documentationPopupsForTOMLOptionsInput { bindSelected(state::documentationPopupsForTOMLOptions) }
                overrideCheckbox(state::documentationPopupsForTOMLOptions)
            }
        }
        
        separator()
        
        row(message("configurations.ruff.logLevel.label")) {
            logLevelInput { bindItem(state::logLevel) }
            overrideCheckbox(state::logLevel)
        }
        row(message("configurations.ruff.logFile.label")) {
            logFileInput { bindText(state::logFile) }
            overrideCheckbox(state::logFile)
        }
        
    }
    
    group(message("configurations.ruff.groups.runOnSave")) {
        row {
            runOnSaveProjectFilesOnlyInput { bindSelected(state::runOnSaveProjectFilesOnly) }
            overrideCheckbox(state::runOnSaveProjectFilesOnly)
        }
        
        separator()
        
        row {
            formatOnSaveInput { bindSelected(state::formatOnSave) }
            overrideCheckbox(state::formatOnSave)
        }
        row {
            fixOnSaveInput { bindSelected(state::fixOnSave) }
            overrideCheckbox(state::fixOnSave)
        }
    }
    
    group(message("configurations.ruff.groups.other")) {
        row {
            label(message("configurations.ruff.subgroups.suggestExecutable.groupLabel"))
        }
        indent {
            row {
                suggestExecutableOnProjectOpenInput { bindSelected(state::suggestExecutableOnProjectOpen) }
                overrideCheckbox(state::suggestExecutableOnProjectOpen)
            }
            row {
                suggestExecutableOnPackagesChangeInput { bindSelected(state::suggestExecutableOnPackagesChange) }
                overrideCheckbox(state::suggestExecutableOnPackagesChange)
            }
        }
        
        row {
            label(message("configurations.ruff.subgroups.folding.groupLabel"))
        }
        indent {
            row {
                foldSingleRuleSelectorsByDefaultInput { bindSelected(state::foldSingleRuleSelectorsByDefault) }
                overrideCheckbox(state::foldSingleRuleSelectorsByDefault)
            }
            row {
                foldNoqaCodesByDefaultInput { bindSelected(state::foldNoqaCodesByDefault) }
                overrideCheckbox(state::foldNoqaCodesByDefault)
            }
        }
    }
    
    advancedSettingsGroup {
        row {
            autoRestartServersInput { bindSelected(state::autoRestartServers) }
            overrideCheckbox(state::autoRestartServers)
        }
        row {
            snoozeFormattingTaskErrorInput { bindSelected(state::snoozeFormattingTaskError) }
            overrideCheckbox(state::snoozeFormattingTaskError)
        }
        row {
            letNativeClientPullDiagnosticsInput { bindSelected(state::letNativeClientPullDiagnostics) }
            overrideCheckbox(state::letNativeClientPullDiagnostics)
        }
    }
    
}


internal fun PanelBasedConfigurable<RuffConfigurations>.createPanel(state: RuffConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return RuffPanel(state, overrides, project).makeComponent()
}
