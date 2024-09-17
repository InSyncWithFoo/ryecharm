package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import insyncwithfoo.ryecharm.configurations.SettingName
import org.junit.Assert.assertEquals
import org.junit.Test


internal class UVConfigurationsTest : ConfigurationsTest<UVConfigurations>() {
    
    override val configurationClass = UVConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 5) {
            assertEquals(null, executable)
            assertEquals(null, configurationFile)
            
            assertEquals(true, packageManaging)
            assertEquals(false, packageManagingNonUVProjects)
            
            assertEquals(emptyMap<SettingName, MillisecondsOrNoLimit>(), timeouts)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.uv.$name.label" }
    }
    
}