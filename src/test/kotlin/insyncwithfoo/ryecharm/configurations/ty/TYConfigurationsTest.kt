package insyncwithfoo.ryecharm.configurations.ty

import com.intellij.json.JsonParser.property
import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class TYConfigurationsTest : ConfigurationsTest<TYConfigurations>() {
    
    override val configurationClass = TYConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 14) {
            assertEquals(null, executable)
            assertEquals(null, configurationFile)
            
            assertEquals(RunningMode.DISABLED, runningMode)
            
            assertEquals(true, enableLanguageServices)
            
            assertEquals(true, diagnostics)
            assertEquals(false, showSyntaxErrors)
            assertEquals(DiagnosticMode.OPEN_FILES_ONLY, diagnosticMode)
            
            assertEquals(true, inlayHints)
            assertEquals(true, inlayHintsVariableTypes)
            assertEquals(true, inlayHintsCallArgumentNames)
            
            assertEquals(true, completions)
            assertEquals(true, completionsAutoImport)
            
            assertEquals(LogLevel.INFO, logLevel)
            assertEquals(null, logFile)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.ty.$name.label" }
    }
    
}
