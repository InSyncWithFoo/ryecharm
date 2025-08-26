package insyncwithfoo.ryecharm.configurations.ruff

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class RuffConfigurationsTest : ConfigurationsTest<RuffConfigurations>() {
    
    override val configurationClass = RuffConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 36) {
            assertEquals(null, executable)
            assertEquals(true, crossPlatformExecutableResolution)
            assertEquals(null, configurationFile)
            
            assertEquals(RunningMode.COMMAND_LINE, runningMode)
            
            assertEquals(true, linting)
            assertEquals(false, showSyntaxErrors)
            assertEquals(false, fileLevelBanner)
            assertEquals(false, renderTooltips)
            assertEquals(TooltipFormat.RULE_MESSAGE, tooltipFormat)
            
            assertEquals(true, quickFixes)
            assertEquals(true, fixAll)
            assertEquals(true, organizeImports)
            assertEquals(true, disableRuleComment)
            assertEquals(true, fixViolation)
            assertEquals(true, fixSimilarViolations)
            assertEquals(false, considerAllFixable)
            
            assertEquals(true, formatting)
            assertEquals(true, formatOnReformat)
            assertEquals(true, formatOnOptimizeImports)
            
            assertEquals(true, documentationPopups)
            assertEquals(true, documentationPopupsForNoqaComments)
            assertEquals(true, documentationPopupsForTOMLRuleCodes)
            assertEquals(true, documentationPopupsForTOMLOptions)
            
            assertEquals(LogLevel.INFO, logLevel)
            assertEquals(null, logFile)
            
            assertEquals(true, runOnSaveProjectFilesOnly)
            assertEquals(false, formatOnSave)
            assertEquals(false, fixOnSave)
            
            assertEquals(true, suggestExecutableOnProjectOpen)
            assertEquals(true, suggestExecutableOnPackagesChange)
            
            assertEquals(true, foldSingleRuleSelectorsByDefault)
            assertEquals(true, foldNoqaCodesByDefault)
            
            assertEquals(true, autoRestartServers)
            assertEquals(false, snoozeFormattingTaskError)
            assertEquals(false, letNativeClientPullDiagnostics)
            assertEquals(false, showImportGraphOnCallHierarchyForFile)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.ruff.$name.label" }
    }
    
}
