package insyncwithfoo.ryecharm.configurations.main

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Test


internal class MainConfigurationsTest : ConfigurationsTest<MainConfigurations>() {
    
    override val configurationClass = MainConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 1) {}
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.main.$name.label" }
    }
    
}
