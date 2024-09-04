package insyncwithfoo.ryecharm.configurations.rye

import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import insyncwithfoo.ryecharm.configurations.SettingName
import org.junit.Assert.assertEquals
import org.junit.Test


internal class RyeConfigurationsTest : ConfigurationsTest<RyeConfigurations>() {
    
    override val configurationClass = RyeConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 2) {
            assertEquals(null, executable)
            
            assertEquals(emptyMap<SettingName, MillisecondsOrNoLimit>(), timeouts)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.rye.$name.label" }
    }
    
}
