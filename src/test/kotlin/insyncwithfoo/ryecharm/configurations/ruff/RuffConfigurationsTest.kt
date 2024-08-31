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
            assertEquals(RunningMode.COMMAND_LINE, runningMode)
            
            assertEquals(null, executable)
            assertEquals(true, crossPlatformExecutableResolution)
            assertEquals(null, configurationFile)
            
            assertEquals(TooltipFormat.RULE_MESSAGE, tooltipFormat)
            
            assertEquals(true, formatOnSave)
            assertEquals(true, formatOnSaveProjectFilesOnly)
            assertEquals(true, formatOnReformat)
            assertEquals(true, optimizeImports)
            
            assertEquals(true, showDocumentationForNoqaCodes)
            assertEquals(true, showDocumentationForTOMLOptions)
            
            assertEquals(true, autoRestartServer)
            
            assertEquals(true, hoverSupport)
            
            assertEquals(true, formattingSupport)
            
            assertEquals(true, diagnosticsSupport)
            assertEquals(false, showSyntaxErrors)
            
            assertEquals(true, codeActionsSupport)
            assertEquals(true, fixAll)
            assertEquals(true, organizeImports)
            assertEquals(true, disableRuleComment)
            assertEquals(true, fixViolation)
            
            assertEquals(LogLevel.INFO, logLevel)
            assertEquals(null, logFile)
            
            assertEquals(true, suggestExecutableOnProjectOpen)
            assertEquals(true, suggestExecutableOnPackagesChange)
            
            assertEquals(emptyMap<SettingName, MillisecondsOrNoLimit>(), timeouts)
        }
    }
    
    @Test
    fun `test - messages`() {
        doMessagesTest { name -> "configurations.ruff.$name.label" }
    }
    
}
