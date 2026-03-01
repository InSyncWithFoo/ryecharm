package insyncwithfoo.ryecharm.ty

import insyncwithfoo.ryecharm.constructorParameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


internal class WorkspaceConfigurationTest {
    
    private lateinit var configuration: WorkspaceConfiguration
    
    @Before
    fun setUp() {
        configuration = WorkspaceConfiguration()
    }
    
    @Test
    fun `test shape`() {
        configuration.apply {
            assertEquals(6, constructorParameters.size)
            
            assertEquals(null, configurationFile)
            assertEquals(false, disableLanguageServices)
            assertEquals(null, diagnosticMode)
            assertEquals(false, showSyntaxErrors)
            
            assertEquals(InlayHints(), configuration.inlayHints)
            assertEquals(Completions(), configuration.completions)
        }
        
        configuration.inlayHints.apply {
            assertEquals(2, constructorParameters.size)
            
            assertEquals(true, variableTypes)
            assertEquals(true, callArgumentNames)
        }
        
        configuration.completions.apply {
            assertEquals(1, constructorParameters.size)
            
            assertEquals(true, autoImport)
        }
    }
    
}
