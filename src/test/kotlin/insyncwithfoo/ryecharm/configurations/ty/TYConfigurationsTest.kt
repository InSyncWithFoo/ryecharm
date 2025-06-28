package insyncwithfoo.ryecharm.configurations.ty

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class TYConfigurationsTest : ConfigurationsTest<TYConfigurations>() {
    
    override val configurationClass = TYConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 2) {
            assertEquals(null, executable)
            assertEquals(RunningMode.DISABLED, runningMode)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.ty.$name.label" }
    }
    
}
