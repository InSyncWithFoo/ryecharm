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
import insyncwithfoo.ryecharm.findRuffExecutableInVenv
import insyncwithfoo.ryecharm.lsp4ijIsAvailable
import insyncwithfoo.ryecharm.lspIsAvailable
import insyncwithfoo.ryecharm.makeFlexible
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.ruff.commands.Ruff
import insyncwithfoo.ryecharm.ruff.commands.detectExecutable
import insyncwithfoo.ryecharm.singleFileTextField


private class RuffPanel(state: RuffConfigurations, overrides: Overrides?, project: Project?) :
    AdaptivePanel<RuffConfigurations>(state, overrides, project)


private fun Row.makeExecutableInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.makeCrossPlatformExecutableResolutionInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.crossPlatformExecutableResolution.label")).apply(block)


private fun Row.makeConfigurationFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Panel.makeRunningModeInputGroup(block: Panel.() -> Unit) =
    buttonsGroup(init = block)


private fun Row.makeLintingInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.linting.label")).apply(block)


private fun Row.makeShowSyntaxErrorsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.showSyntaxErrors.label")).apply(block)


private fun Row.makeTooltipFormatInput(block: Cell<ComboBox<TooltipFormat>>.() -> Unit) =
    comboBox<TooltipFormat>().label(message("configurations.ruff.tooltipFormat.label")).apply(block)


private fun Row.makeQuickFixesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.quickFixes.label")).apply(block)


private fun Row.makeFixAllInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixAll.label")).apply(block)


private fun Row.makeOrganizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.organizeImports.label")).apply(block)


private fun Row.makeDisableRuleCommentInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.disableRuleComment.label")).apply(block)


private fun Row.makeFixViolationInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixViolation.label")).apply(block)


private fun Row.makeFormattingInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatting.label")).apply(block)


private fun Row.makeFormatOnReformatInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnReformat.label")).apply(block)


private fun Row.makeFormatOnOptimizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnOptimizeImports.label")).apply(block)


private fun Row.makeFormatOnSaveInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnSave.label")).apply(block)


private fun Row.makeFormatOnSaveProjectFilesOnlyInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnSaveProjectFilesOnly.label")).apply(block)


private fun Row.makeDocumentationPopupsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopups.label")).apply(block)


private fun Row.makeDocumentationPopupsForNoqaCommentsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopupsForNoqaComments.label")).apply(block)


private fun Row.makeDocumentationPopupsForTOMLOptionsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.documentationPopupsForTOMLOptions.label")).apply(block)


private fun Row.makeLogLevelInput(block: Cell<ComboBox<LogLevel>>.() -> Unit) =
    comboBox<LogLevel>().apply(block)


private fun Row.makeLogFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.makeSuggestExecutableOnProjectOpenInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnProjectOpen.label")).apply(block)


private fun Row.makeSuggestExecutableOnPackagesChangeInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnPackagesChange.label")).apply(block)


private fun Row.makeAutoRestartServersInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.autoRestartServers.label")).apply(block)


@Suppress("DialogTitleCapitalization")
private fun RuffPanel.makeComponent() = panel {
    
    row(message("configurations.ruff.executable.label")) {
        makeExecutableInput {
            val detectedExecutable = Ruff.detectExecutable()?.toString()
                ?: project?.findRuffExecutableInVenv()?.toString()
            
            bindText(state::executable) { detectedExecutable.orEmpty() }
            emptyText = detectedExecutable ?: message("configurations.ruff.executable.placeholder")
        }
        makeOverrideCheckboxIfApplicable(state::executable)
    }
    row("") {
        makeCrossPlatformExecutableResolutionInput { bindSelected(state::crossPlatformExecutableResolution) }
        makeOverrideCheckboxIfApplicable(state::crossPlatformExecutableResolution)
    }
    
    row(message("configurations.ruff.configurationFile.label")) {
        makeConfigurationFileInput { bindText(state::configurationFile) }
        makeOverrideCheckboxIfApplicable(state::configurationFile)
    }
    
    val runningModeInputGroup = makeRunningModeInputGroup {
        row(message("configurations.ruff.runningMode.label")) {
            radioButtonFor(RunningMode.COMMAND_LINE)
            radioButtonFor(RunningMode.LSP4IJ) { label ->
                message("configurations.ruff.runningMode.unavailable", label).takeUnless { lsp4ijIsAvailable }
            }
            radioButtonFor(RunningMode.LSP) { label ->
                message("configurations.ruff.runningMode.unavailable", label).takeUnless { lspIsAvailable }
            }
            
            makeOverrideCheckboxIfApplicable(state::runningMode)
        }
    }
    runningModeInputGroup.bindSelected(state::runningMode)
    
    group(message("configurations.ruff.groups.main")) {
        
        row {
            makeLintingInput { bindSelected(state::linting) }
            makeOverrideCheckboxIfApplicable(state::linting)
        }
        indent {
            row {
                makeShowSyntaxErrorsInput { bindSelected(state::showSyntaxErrors) }
                makeOverrideCheckboxIfApplicable(state::showSyntaxErrors)
            }
            row {
                makeTooltipFormatInput { bindItem(state::tooltipFormat) }
                makeOverrideCheckboxIfApplicable(state::tooltipFormat)
            }
        }
        
        row {
            makeQuickFixesInput { bindSelected(state::quickFixes) }
            makeOverrideCheckboxIfApplicable(state::quickFixes)
        }
        indent {
            row {
                makeFixAllInput { bindSelected(state::fixAll) }
                makeOverrideCheckboxIfApplicable(state::fixAll)
            }
            row {
                makeOrganizeImportsInput { bindSelected(state::organizeImports) }
                makeOverrideCheckboxIfApplicable(state::organizeImports)
            }
            row {
                makeDisableRuleCommentInput { bindSelected(state::disableRuleComment) }
                makeOverrideCheckboxIfApplicable(state::disableRuleComment)
            }
            row {
                makeFixViolationInput { bindSelected(state::fixViolation) }
                makeOverrideCheckboxIfApplicable(state::fixViolation)
            }
        }
        
        row {
            makeFormattingInput { bindSelected(state::formatting) }
            makeOverrideCheckboxIfApplicable(state::formatting)
        }
        indent {
            row {
                makeFormatOnReformatInput { bindSelected(state::formatOnReformat) }
                makeOverrideCheckboxIfApplicable(state::formatOnReformat)
            }
            row {
                makeFormatOnOptimizeImportsInput { bindSelected(state::formatOnOptimizeImports) }
                makeOverrideCheckboxIfApplicable(state::formatOnOptimizeImports)
            }
            row {
                makeFormatOnSaveInput { bindSelected(state::formatOnSave) }
                makeOverrideCheckboxIfApplicable(state::formatOnSave)
            }
            indent {
                row {
                    makeFormatOnSaveProjectFilesOnlyInput { bindSelected(state::formatOnSaveProjectFilesOnly) }
                    makeOverrideCheckboxIfApplicable(state::formatOnSaveProjectFilesOnly)
                }
            }
        }
        
        row {
            makeDocumentationPopupsInput { bindSelected(state::documentationPopups) }
            makeOverrideCheckboxIfApplicable(state::documentationPopups)
        }
        indent {
            row {
                makeDocumentationPopupsForNoqaCommentsInput { bindSelected(state::documentationPopupsForNoqaComments) }
                makeOverrideCheckboxIfApplicable(state::documentationPopupsForNoqaComments)
            }
            row {
                makeDocumentationPopupsForTOMLOptionsInput { bindSelected(state::documentationPopupsForTOMLOptions) }
                makeOverrideCheckboxIfApplicable(state::documentationPopupsForTOMLOptions)
            }
        }
        
        separator()
        
        row(message("configurations.ruff.logLevel.label")) {
            makeLogLevelInput { bindItem(state::logLevel) }
            makeOverrideCheckboxIfApplicable(state::logLevel)
        }
        row(message("configurations.ruff.logFile.label")) {
            makeLogFileInput { bindText(state::logFile) }
            makeOverrideCheckboxIfApplicable(state::logFile)
        }
        
    }
    
    group(message("configurations.ruff.groups.other")) {
        row {
            label(message("configurations.ruff.subgroups.suggestExecutable.groupLabel"))
        }
        indent {
            row {
                makeSuggestExecutableOnProjectOpenInput { bindSelected(state::suggestExecutableOnProjectOpen) }
                makeOverrideCheckboxIfApplicable(state::suggestExecutableOnProjectOpen)
            }
            row {
                makeSuggestExecutableOnPackagesChangeInput { bindSelected(state::suggestExecutableOnPackagesChange) }
                makeOverrideCheckboxIfApplicable(state::suggestExecutableOnPackagesChange)
            }
        }
    }
    
    collapsibleGroup(message("configurations.ruff.groups.advanced")) {
        row {
            makeAutoRestartServersInput { bindSelected(state::autoRestartServers) }
            makeOverrideCheckboxIfApplicable(state::autoRestartServers)
        }
    }
    
    makeTimeoutGroup(state.timeouts, RuffTimeouts.entries)
    
}


internal fun PanelBasedConfigurable<RuffConfigurations>.createPanel(state: RuffConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return RuffPanel(state, overrides, project).makeComponent()
}
