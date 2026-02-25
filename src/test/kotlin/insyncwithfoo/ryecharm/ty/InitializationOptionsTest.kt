package insyncwithfoo.ryecharm.ty

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
            assertEquals(2, constructorParameters.size)
            
            assertEquals(null, logLevel)
            assertEquals(null, logFile)
        }
    }
    
}
