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


private fun Row.makeTooltipFormatInput(block: Cell<ComboBox<TooltipFormat>>.() -> Unit) =
    comboBox<TooltipFormat>().apply(block)


private fun Row.makeFormatOnSaveInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnSave.label")).apply(block)


private fun Row.makeFormatOnSaveProjectFilesOnlyInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnSaveProjectFilesOnly.label")).apply(block)


private fun Row.makeFormatOnReformatInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formatOnReformat.label")).apply(block)


private fun Row.makeOptimizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.optimizeImports.label")).apply(block)


private fun Row.makeShowDocumentationForNoqaCodesInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.showDocumentationForNoqaCodes.label")).apply(block)


private fun Row.makeShowDocumentationForTOMLOptionsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.showDocumentationForTOMLOptions.label")).apply(block)


private fun Row.makeAutoRestartServersInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.autoRestartServer.label")).apply(block)


private fun Row.makeHoverSupportInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.hoverSupport.label")).apply(block)


private fun Row.makeFormattingSupportInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.formattingSupport.label")).apply(block)


private fun Row.makeDiagnosticsSupportInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.diagnosticsSupport.label")).apply(block)


private fun Row.makeShowSyntaxErrorsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.showSyntaxErrors.label")).apply(block)


private fun Row.makeCodeActionsSupportInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.codeActionsSupport.label")).apply(block)


private fun Row.makeFixAllInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixAll.label")).apply(block)


private fun Row.makeOrganizeImportsInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.organizeImports.label")).apply(block)


private fun Row.makeDisableRuleCommentInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.disableRuleComment.label")).apply(block)


private fun Row.makeFixViolationInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.fixViolation.label")).apply(block)


private fun Row.makeLogLevelInput(block: Cell<ComboBox<LogLevel>>.() -> Unit) =
    comboBox<LogLevel>().apply(block)


private fun Row.makeLogFileInput(block: Cell<TextFieldWithBrowseButton>.() -> Unit) =
    singleFileTextField().makeFlexible().apply(block)


private fun Row.makeSuggestExecutableOnProjectOpenInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnProjectOpen.label")).apply(block)


private fun Row.makeSuggestExecutableOnPackagesChangeInput(block: Cell<JBCheckBox>.() -> Unit) =
    checkBox(message("configurations.ruff.suggestExecutableOnPackagesChange.label")).apply(block)


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
            radioButtonFor(RunningMode.NO_LINTING)
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
    
    group(message("configurations.ruff.groups.tooltips")) {
        row(message("configurations.ruff.tooltipFormat.label")) {
            makeTooltipFormatInput { bindItem(state::tooltipFormat) }
            makeOverrideCheckboxIfApplicable(state::tooltipFormat)
        }
    }
    
    group(message("configurations.ruff.groups.formatting")) {
        row {
            label(message("configurations.ruff.groups.formatting.groupLabel"))
        }
        indent {
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
            row {
                makeFormatOnReformatInput { bindSelected(state::formatOnReformat) }
                makeOverrideCheckboxIfApplicable(state::formatOnReformat)
            }
            row {
                makeOptimizeImportsInput { bindSelected(state::optimizeImports) }
                makeOverrideCheckboxIfApplicable(state::optimizeImports)
            }
        }
    }
    
    group(message("configurations.ruff.groups.documentation")) {
        row {
            label(message("configurations.ruff.groups.documentation.groupLabel"))
        }
        indent {
            row {
                makeShowDocumentationForNoqaCodesInput { bindSelected(state::showDocumentationForNoqaCodes) }
                makeOverrideCheckboxIfApplicable(state::showDocumentationForNoqaCodes)
            }
            row {
                makeShowDocumentationForTOMLOptionsInput { bindSelected(state::showDocumentationForTOMLOptions) }
                makeOverrideCheckboxIfApplicable(state::showDocumentationForTOMLOptions)
            }
        }
    }
    
    group(message("configurations.ruff.groups.languageServer")) {
        
        row {
            makeAutoRestartServersInput { bindSelected(state::autoRestartServer) }
            makeOverrideCheckboxIfApplicable(state::autoRestartServer)
        }
        
        separator()
        
        row {
            makeHoverSupportInput { bindSelected(state::hoverSupport) }
            makeOverrideCheckboxIfApplicable(state::hoverSupport)
        }
        
        row {
            makeFormattingSupportInput { bindSelected(state::formattingSupport) }
            makeOverrideCheckboxIfApplicable(state::formattingSupport)
        }
        
        row {
            makeDiagnosticsSupportInput { bindSelected(state::diagnosticsSupport) }
            makeOverrideCheckboxIfApplicable(state::diagnosticsSupport)
        }
        indent {
            row {
                makeShowSyntaxErrorsInput { bindSelected(state::showSyntaxErrors) }
                makeOverrideCheckboxIfApplicable(state::showSyntaxErrors)
            }
        }
        
        row {
            makeCodeActionsSupportInput { bindSelected(state::codeActionsSupport) }
            makeOverrideCheckboxIfApplicable(state::codeActionsSupport)
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
            label(message("configurations.ruff.groups.suggestExecutable.groupLabel"))
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
    
    makeTimeoutGroup(state.timeouts, RuffTimeouts.entries)
    
}


internal fun PanelBasedConfigurable<RuffConfigurations>.createPanel(state: RuffConfigurations): DialogPanel {
    val (project, overrides) = projectAndOverrides
    return RuffPanel(state, overrides, project).makeComponent()
}
