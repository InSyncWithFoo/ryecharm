package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.constructorParameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


internal class InitializationOptionsTest {
    
    private lateinit var options: InitializationOptions
    
    @Before
    fun setUp() {
        options = InitializationOptions()
    }
    
    @Test
    fun `test shape`() {
        options.apply {
            assertEquals(8, constructorParameters.size)
            
            assertEquals(null, configuration)
            assertEquals(true, fixAll)
            assertEquals(true, organizeImports)
            assertEquals(true, showSyntaxErrors)
            assertEquals(null, logLevel)
            assertEquals(null, logFile)
            assertEquals(CodeAction(), codeAction)
            assertEquals(Lint(), lint)
        }
        
        options.codeAction.apply {
            assertEquals(2, constructorParameters.size)
            
            assertEquals(1, disableRuleComment.constructorParameters.size)
            assertEquals(1, fixViolation.constructorParameters.size)
            
            assertEquals(true, disableRuleComment.enable)
            assertEquals(true, fixViolation.enable)
        }
        
        options.lint.apply {
            assertEquals(1, constructorParameters.size)
            
            assertEquals(true, enable)
        }
    }
    
}
