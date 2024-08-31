package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.configurations.ruff.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter


private inline val <reified T : Any> T.constructorParameters: List<KParameter>
    get() = T::class.constructors.toList<KFunction<Any>>().first().parameters


internal class InitializationOptionsTest {
    
    private lateinit var options: InitializationOptions
    private lateinit var codeAction: CodeAction
    private lateinit var lint: Lint
    
    @Before
    fun setUp() {
        options = InitializationOptions()
        codeAction = options.codeAction
        lint = options.lint
    }
    
    @Test
    fun `test - shape`() {
        options.apply {
            assertEquals(8, constructorParameters.size)
            
            assertEquals(null, configuration)
            assertEquals(true, fixAll)
            assertEquals(true, organizeImports)
            assertEquals(true, showSyntaxErrors)
            assertEquals(LogLevel.INFO.value, logLevel)
            assertEquals(null, logFile)
            assertEquals(CodeAction(), codeAction)
            assertEquals(Lint(), lint)
        }
        
        codeAction.apply {
            assertEquals(2, constructorParameters.size)
            
            assertEquals(1, disableRuleComment.constructorParameters.size)
            assertEquals(1, fixViolation.constructorParameters.size)
            
            assertEquals(true, disableRuleComment.enable)
            assertEquals(true, fixViolation.enable)
        }
        
        lint.apply {
            assertEquals(1, constructorParameters.size)
            
            assertEquals(true, enable)
        }
    }
    
}
