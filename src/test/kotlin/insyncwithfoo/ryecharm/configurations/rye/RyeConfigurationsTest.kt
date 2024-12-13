package insyncwithfoo.ryecharm.configurations.rye

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class RyeConfigurationsTest : ConfigurationsTest<RyeConfigurations>() {
    
    override val configurationClass = RyeConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 1) {
            assertEquals(null, executable)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.rye.$name.label" }
    }
    
}
