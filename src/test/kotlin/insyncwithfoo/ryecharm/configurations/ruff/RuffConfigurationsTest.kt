package insyncwithfoo.ryecharm.configurations.ruff

import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import insyncwithfoo.ryecharm.configurations.SettingName
import org.junit.Assert.assertEquals
import org.junit.Test


internal class RuffConfigurationsTest : ConfigurationsTest<RuffConfigurations>() {
    
    override val configurationClass = RuffConfigurations::class
    
    @Test
    fun `test - shape`() {
        doShapeTest(expectedSize = 26) {
            assertEquals(null, executable)
            assertEquals(true, crossPlatformExecutableResolution)
            assertEquals(null, configurationFile)
            
            assertEquals(RunningMode.COMMAND_LINE, runningMode)
            
            assertEquals(true, linting)
            assertEquals(false, showSyntaxErrors)
            assertEquals(TooltipFormat.RULE_MESSAGE, tooltipFormat)
            
            assertEquals(true, quickFixes)
            assertEquals(true, fixAll)
            assertEquals(true, organizeImports)
            assertEquals(true, disableRuleComment)
            assertEquals(true, fixViolation)
            
            assertEquals(true, formatting)
            assertEquals(true, formatOnReformat)
            assertEquals(true, formatOnOptimizeImports)
            assertEquals(true, formatOnSave)
            assertEquals(true, formatOnSaveProjectFilesOnly)
            
            assertEquals(true, documentationPopups)
            assertEquals(true, documentationPopupsForNoqaComments)
            assertEquals(true, documentationPopupsForTOMLOptions)
            
            assertEquals(LogLevel.INFO, logLevel)
            assertEquals(null, logFile)
            
            assertEquals(true, suggestExecutableOnProjectOpen)
            assertEquals(true, suggestExecutableOnPackagesChange)
            
            assertEquals(true, autoRestartServers)
            
            assertEquals(emptyMap<SettingName, MillisecondsOrNoLimit>(), timeouts)
        }
    }
    
    @Test
    fun `test - messages`() {
        doMessagesTest { name -> "configurations.ruff.$name.label" }
    }
    
}
