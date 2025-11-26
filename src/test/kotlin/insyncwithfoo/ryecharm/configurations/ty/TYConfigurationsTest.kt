package insyncwithfoo.ryecharm.configurations.ty

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class TYConfigurationsTest : ConfigurationsTest<TYConfigurations>() {
    
    override val configurationClass = TYConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 9) {
            assertEquals(null, executable)
            assertEquals(RunningMode.DISABLED, runningMode)
            
            assertEquals(true, diagnostics)
            assertEquals(DiagnosticMode.OPEN_FILES_ONLY, diagnosticMode)
            
            assertEquals(true, inlayHints)
            assertEquals(true, inlayHintsVariableTypes)
            assertEquals(true, inlayHintsCallArgumentNames)
            
            assertEquals(false, experimentalRename)
            assertEquals(false, experimentalAutoImport)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.ty.$name.label" }
    }
    
}
