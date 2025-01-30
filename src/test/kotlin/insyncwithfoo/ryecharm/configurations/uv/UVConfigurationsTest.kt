package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class UVConfigurationsTest : ConfigurationsTest<UVConfigurations>() {
    
    override val configurationClass = UVConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 4) {
            assertEquals(null, executable)
            assertEquals(null, configurationFile)
            
            assertEquals(false, retrieveDependenciesInReadAction)
            assertEquals(5, dependenciesDataMaxAge)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.uv.$name.label" }
    }
    
}
